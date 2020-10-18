package com.marsdl.recommand.remmand;

import com.alibaba.fastjson.JSON;
import com.marsdl.recommand.dao.Dao;
import com.marsdl.recommand.dao.redis.RedisDao;
import com.marsdl.recommand.entity.ItemUserList;
import com.marsdl.recommand.entity.RelateCountMatrixEntity;
import com.marsdl.recommand.entity.UserItemList;
import com.marsdl.recommand.remmand.common.Recommand;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class UserCF extends Recommand {
    private static final Logger logger = LogManager.getLogger(UserCF.class);

    @Resource
    private Dao dao;
    @Resource
    private RedisDao redisDao;

    @Value("${mongodb.recommand.user_item_list_collection}")
    private String userItemListCollection;
    @Value("${mongodb.recommand.item_user_list_collection}")
    private String itemUserListCollection;
    @Value("${mongodb.recommand.user_and_user_item_list_matrix}")
    private String userAndUserItemListMatrix;

    //用户与用户之间的关联程度矩阵队列，消费者消费后存放库中
    private Queue<RelateCountMatrixEntity> queue = new ConcurrentLinkedQueue<>();

    //需要在启动取出所有商品对应的用户数量，商品数量要小于10万条
    private Map<String, Integer> itemUserListCountMap = new ConcurrentHashMap<>();

    /**
     * 读取列表与商品表的生成矩阵方法
     * 这种方法要被废弃掉，速度非常慢，非常占用内存
     * <p>
     * 如果您想要理解基于用户的协同过滤推荐算法，可以参考该方法
     */
    @Deprecated
    void createUserAndUserItemListMapMatrix() {
        long startTime = System.currentTimeMillis();
        List<UserItemList> userItemListList = dao.findByCursorId(null, UserItemList.class, userItemListCollection);
        int sum = 0;
        while (!CollectionUtils.isEmpty(userItemListList)) {
            sum += userItemListList.size();
            logger.info("已经建立用户数量: {}", sum);
            List<UserItemList> finalUserItemListList = userItemListList;
            finalUserItemListList.parallelStream().forEach(this::matrix);
            String lastId = userItemListList.get(userItemListList.size() - 1).get_id();
            userItemListList = dao.findByCursorId(lastId, UserItemList.class, userItemListCollection);
        }
        long endTime = System.currentTimeMillis() - startTime;
        logger.info("建立用户矩阵耗时(ms): {}", endTime);
    }

    //基于redis的矩阵建立方法，速度很慢，保存的数据可以压缩到最小
    @Deprecated
    private void matrix(UserItemList userItemList) {
        long startTime = System.currentTimeMillis();
        //产品列表集合
        List<String> itemList = userItemList.getItemList();
        Map<String, Integer> userItemListMap = new ConcurrentHashMap<>();

        itemList.parallelStream().forEach(item -> {
            ItemUserList itemUserList = dao.findItemUserListById(item, itemUserListCollection);
            if (itemUserList == null) {
                return;
            }

            itemUserList.getUserList()
                    .stream()
                    .filter(e -> !redisDao.hasKey(e, userItemList.getUserId()) && !e.equals(userItemList.getUserId()))
                    .forEach(itemUser -> {
                        Integer count = userItemListMap.get(itemUser);
                        if (count == null) {
                            userItemListMap.put(itemUser, 1);
                            return;
                        }
                        count++;
                        userItemListMap.put(itemUser, count);
                    });
        });

        Set<Map.Entry<String, Integer>> entrySet = userItemListMap.entrySet();
        for (Map.Entry<String, Integer> userItemListMapEntry : entrySet) {
            redisDao.saveStrValue(userItemList.getUserId(), userItemListMapEntry.getKey());
        }
        dao.updateRelateCountMatrix(userItemList.getUserId(), userItemListMap, userAndUserItemListMatrix);
        long wasteTime = System.currentTimeMillis() - startTime;

        logger.info("userId: {}, userItemListMap size: {}, wasteTime(ms): {}",
                userItemList.getUserId(), entrySet.size(), wasteTime);
    }

    //根据数据库得出
    public void createItemUserListCountMap() {
        long startTime = System.currentTimeMillis();
        List<ItemUserList> itemUserListList = dao.findByCursorId(null, ItemUserList.class, itemUserListCollection);
        while (!CollectionUtils.isEmpty(itemUserListList)) {
            itemUserListList.parallelStream()
                    .forEach(item -> itemUserListCountMap.put(item.getItemId(), item.getUserList().size()));

            String lastId = itemUserListList.get(itemUserListList.size() - 1).get_id();
            itemUserListList = dao.findByCursorId(lastId, ItemUserList.class, itemUserListCollection);
        }
        long endTime = System.currentTimeMillis() - startTime;
        logger.info("建立用户矩阵耗时(ms): {}", endTime);
    }

    /**
     * 读取列表生成矩阵方法
     */
    public void createMatrix() {
        //每次400个与全部结果保存
        new Thread(this::consumerQueue).start();
        long startTime = System.currentTimeMillis();

        String lastId = userAndUserMatrix(null);
        while (StringUtils.isNotBlank(lastId)) {
            lastId = userAndUserMatrix(lastId);
        }
        //存放到队列中最后一条数据
        queue.offer(new RelateCountMatrixEntity(null, null, -1));

        long wasteTime = System.currentTimeMillis() - startTime;
        logger.info("消耗时间：{}", wasteTime);
    }

    //用户与用户的关联程度计算方法，会将结果存放在队列中，供消费者进程消费后进行其他操作
    private String userAndUserMatrix(String id) {
        List<UserItemList> userItemListList = dao.findByCursorId(id, UserItemList.class, userItemListCollection);
        if (CollectionUtils.isEmpty(userItemListList)) {
            return null;
        }
        List<UserItemList> subList = userItemListList;
        while (!CollectionUtils.isEmpty(userItemListList)) {
            //比较 subList中的用户产品列表 与 全部用户产品列表 每个用户与用户之间的相同产品个数
            for (UserItemList item : userItemListList) {
                for (UserItemList currentUserItemList : subList) {
                    if (currentUserItemList.getUserId().equals(item.getUserId()) ||
                            CollectionUtils.isEmpty(item.getItemList()) ||
                            CollectionUtils.isEmpty(currentUserItemList.getItemList())) {

                        continue;
                    }
//                    int totalSame = countListTotalSame(item.getItemList(), currentUserItemList.getItemList());
                    double totalSameScore = countListTotalSameScore(item.getItemList(), currentUserItemList.getItemList());
                    double relateScore = totalSameScore / (Math.sqrt(item.getItemList().size()) * Math.sqrt(currentUserItemList.getItemList().size()));
                    if (totalSameScore > 0) {
                        queue.offer(new RelateCountMatrixEntity(item.getUserId(), currentUserItemList.getUserId(), 0, relateScore));
                    }
                }
            }
            String lastId = userItemListList.get(userItemListList.size() - 1).get_id();
            userItemListList = dao.findByCursorId(lastId, UserItemList.class, userItemListCollection);
        }
        return subList.get(subList.size() - 1).get_id();
    }



    //关联程度的分子，惩罚了用户 u 和用户 v 共同兴趣列表中热门物品对他们相似度的影响
    private double countListTotalSameScore(List<String> primaryItemList, List<String> secondaryItemList) {
        List<String> sameItemList = new ArrayList<>();
        for (String primaryItem : primaryItemList) {
            if (secondaryItemList.contains(primaryItem)) {
                sameItemList.add(primaryItem);
            }
        }
        double sum = 0;
        for (String itemId : sameItemList) {
            Integer userListCount = itemUserListCountMap.get(itemId);
            if (userListCount == null) {
                continue;
            }
            int logCount = 1 + userListCount;
            if (logCount > 1) {
                sum += 1 / Math.log10(logCount);
            }
        }
        return sum;
    }

    //消费队列数据
    private void consumerQueue() {
        long startTime = System.currentTimeMillis();
        comsumer(dao, queue, userAndUserItemListMatrix);
        long endTime = System.currentTimeMillis() - startTime;
        logger.info("计算用户矩阵消耗时间: {}", endTime);
    }
}
