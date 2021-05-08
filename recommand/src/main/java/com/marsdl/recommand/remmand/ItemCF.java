package com.marsdl.recommand.remmand;

import com.alibaba.fastjson.JSON;
import com.marsdl.recommand.dao.Dao;
import com.marsdl.recommand.entity.ItemUserList;
import com.marsdl.recommand.entity.KeyMatrixEntity;
import com.marsdl.recommand.entity.RelateCountMatrixEntity;
import com.marsdl.recommand.remmand.common.Recommand;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class ItemCF extends Recommand {
    private static final Logger logger = LogManager.getLogger(ItemCF.class);

    @Resource
    private Dao dao;
    @Value("${mongodb.recommand.user_item_list_collection}")
    private String userItemListCollection;
    @Value("${mongodb.recommand.item_user_list_collection}")
    private String itemUserListCollection;
    @Value("${mongodb.recommand.item_and_item_user_list_matrix}")
    private String itemAndItemUserListMatrix;

    //用户与用户之间的关联程度矩阵队列，消费者消费后存放库中
    private Queue<KeyMatrixEntity> queue = new ConcurrentLinkedQueue<>();

    public void createMatrix() {
        //每次400个与全部结果保存
        new Thread(this::consumerQueue).start();
        long startTime = System.currentTimeMillis();

        String lastId = itemAndItemMatrix(null);
        while (StringUtils.isNotBlank(lastId)) {
            lastId = itemAndItemMatrix(lastId);
        }
        //存放到队列中最后一条数据
        queue.offer(new KeyMatrixEntity("-1"));

        long wasteTime = System.currentTimeMillis() - startTime;
        logger.info("消耗时间：{}", wasteTime);
    }

    /**
     * item与item的关联程度计算方法，会将结果存放在队列中，供消费者进程消费后进行其他操作
     */
    private String itemAndItemMatrix(String id) {
        List<ItemUserList> itemUserListList = dao.findByCursorId(id, ItemUserList.class, itemUserListCollection);
        if (CollectionUtils.isEmpty(itemUserListList)) {
            return null;
        }
        Map<String, List<RelateCountMatrixEntity>> itemMatrixListMap = new HashMap<>();
        int sK = 100;

        List<ItemUserList> subList = itemUserListList;
        while (!CollectionUtils.isEmpty(itemUserListList)) {

            //比较 subList中的产品用户列表 与 全部用户产品列表 每个产品与产品之间的相同产品个数
            for (ItemUserList item : itemUserListList) {
                for (ItemUserList currentItemUserList : subList) {
                    if (currentItemUserList.getItemId().equals(item.getItemId()) ||
                            CollectionUtils.isEmpty(item.getUserList()) ||
                            CollectionUtils.isEmpty(currentItemUserList.getUserList())) {

                        continue;
                    }
                    //计算两个产品的相似度
                    int totalSameScore = countListTotalSame(item.getUserList(), currentItemUserList.getUserList());
                    double relateScore = totalSameScore / (Math.sqrt(item.getUserList().size()) * Math.sqrt(currentItemUserList.getUserList().size()));
                    if (relateScore <= 0) {
                        continue;
                    }
                    //将产品的相似度放入列表进行下次比较
                    List<RelateCountMatrixEntity> matrixList = itemMatrixListMap.get(currentItemUserList.getItemId());
                    if (CollectionUtils.isEmpty(matrixList)) {
                        matrixList = new ArrayList<>();
                        matrixList.add(new RelateCountMatrixEntity(item.getItemId(), currentItemUserList.getItemId(), totalSameScore, relateScore));
                        itemMatrixListMap.put(currentItemUserList.getItemId(), matrixList);
                    }
                    sort(new RelateCountMatrixEntity(item.getItemId(), currentItemUserList.getItemId(), totalSameScore, relateScore), matrixList);
                    if (matrixList.size() > sK) {
                        matrixList.subList(sK, matrixList.size()).clear();
                    }
                }
            }
            String lastId = itemUserListList.get(itemUserListList.size() - 1).get_id();
            itemUserListList = dao.findByCursorId(lastId, ItemUserList.class, itemUserListCollection);
        }
        for (Map.Entry<String, List<RelateCountMatrixEntity>> entry : itemMatrixListMap.entrySet()) {
            queue.offer(new KeyMatrixEntity(entry.getKey(), entry.getValue()));
        }
        return subList.get(subList.size() - 1).get_id();
    }

    //插入排序
    //insertion sorting
    private void sort(RelateCountMatrixEntity entity, List<RelateCountMatrixEntity> list) {
        list.add(entity);
        int size = list.size() - 1;
        if (size == 0) {
            return;
        }
        int index = size;
        while (list.get(size).getRelateScore() > list.get(index - 1).getRelateScore()) {
            index--;
            if (index <= 0) {
                break;
            }
        }
        for (int i = size; i > index; i--) {
            list.set(i, list.get(i - 1));
        }
        list.set(index, entity);
    }

    //消费队列的数据
    private void consumerQueue() {
        long startTime = System.currentTimeMillis();
        while (true) {
            KeyMatrixEntity entity = queue.poll();
            if (entity == null) {
                try {
                    logger.info("queue data is null, waiting 500ms...");
                    Thread.sleep(500);
                } catch (Exception e) {
                    //
                }
            }
            //判断队列中是否是最后一条记录
            if (entity != null && "-1".equals(entity.getKey())) {
                break;
            }
            if (entity != null) {
                dao.saveEntity(entity, itemAndItemUserListMatrix);
                String result = JSON.toJSONString(entity);
                logger.info(result);
            }
        }
        long endTime = System.currentTimeMillis() - startTime;
        logger.info("create matrix waste time : {}", endTime);
    }


    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.subList(4, list.size()).clear();
        System.out.println(list);
    }

}
