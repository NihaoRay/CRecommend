package com.marsdl.recommand.api;

import com.alibaba.fastjson.JSON;
import com.marsdl.recommand.dao.Dao;
import com.marsdl.recommand.entity.RelateCountMatrixEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class RecommandTest {

    @Resource
    private Dao dao;

    @Test
    void recommand() {
        //传递一个用户，推荐N条相关产品
        long statTime = System.currentTimeMillis();
        String userId = "404";
        List<RelateCountMatrixEntity> list = dao.findRelateCountMatrixByAxis(userId, "user_and_user_item_list_matrix_test");

        List<String> indexList = new ArrayList<>();
        List<RelateCountMatrixEntity> relateList = new ArrayList<>();
        for (RelateCountMatrixEntity entity : list) {
            String indexUserId = entity.getyAxis();
            if (!userId.equals(entity.getxAxis())) {
                indexUserId = entity.getxAxis();
            }
            if (indexList.contains(indexUserId)) {
                continue;
            }
            indexList.add(indexUserId);
            sort(entity, relateList);
        }
        long endTime = System.currentTimeMillis() - statTime;
        System.out.println("wasteTime: " + endTime);
        System.out.println(JSON.toJSONString(relateList));
        //得到已经排序完成的列表后relateList ，可以根据用户的id查询对应的商品列表，然后返回，这样UserCF推荐完成
    }

    //插入排序
    private List<RelateCountMatrixEntity> sort(RelateCountMatrixEntity entity,
                                               List<RelateCountMatrixEntity> relateList) {
        //添位置
        relateList.add(entity);
        int size = relateList.size() - 1;
        if (size == 0) {
            return relateList;
        }
        //找位置
        int index = size;
        while (relateList.get(size).getRelateScore() > relateList.get(index - 1).getRelateScore()) {
            index--;
            if (index <= 0) {
                break;
            }
        }
        //进入位置
        for (int i = size; i > index; i--) {
            relateList.set(i, relateList.get(i - 1));
        }
        relateList.set(index, entity);
        return relateList;
    }


//    public static void main(String[] args) {
//        RelateCountMatrixEntity entity = new RelateCountMatrixEntity(0.01);
//        RelateCountMatrixEntity entity1 = new RelateCountMatrixEntity(0.334);
//        RelateCountMatrixEntity entity2 = new RelateCountMatrixEntity(0.23);
//        RelateCountMatrixEntity entity3 = new RelateCountMatrixEntity(0.11);
//        RelateCountMatrixEntity entity4 = new RelateCountMatrixEntity(0.31);
//        RelateCountMatrixEntity entity5 = new RelateCountMatrixEntity(0.11);
//
//        List<RelateCountMatrixEntity> list = new ArrayList<>();
//        sort(entity, list);
//        System.out.println(JSON.toJSONString(list));
//        sort(entity1, list);
//        System.out.println(JSON.toJSONString(list));
//        sort(entity2, list);
//        System.out.println(JSON.toJSONString(list));
//        sort(entity3, list);
//        System.out.println(JSON.toJSONString(list));
//        sort(entity4, list);
//        System.out.println(JSON.toJSONString(list));
//        sort(entity5, list);
//        System.out.println(JSON.toJSONString(list));
//    }


}