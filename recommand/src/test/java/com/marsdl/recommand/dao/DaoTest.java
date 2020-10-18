package com.marsdl.recommand.dao;

import com.alibaba.fastjson.JSON;
import com.marsdl.recommand.entity.UserItemList;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class DaoTest {

    @Resource
    private Dao dao;

    @Value("${mongodb.recommand.user_item_list_collection}")
    private String userItemListCollection;
    @Value("${mongodb.recommand.item_user_list_collection}")
    private String itemUserListCollection;

    @Test
    void savaMap() {


    }

    @Test
    void findByCursorId() {
        List<UserItemList> userItemList = dao.findByCursorId(null, UserItemList.class, userItemListCollection);
        userItemList.forEach(item -> {
            System.out.println(JSON.toJSONString(item));
        });


        System.out.println("----------------------");
    }


    @Test
    void update() {
        UserItemList userItemList = new UserItemList();
        userItemList.setUserId("1");
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        Map<String, List<String>> userMap = new HashMap<>();
        userMap.put("1", list);

        userItemList.setItemList(list);

//        dao.update("1", userMap, "user_and_user_item_list_matrix");
    }

    @Test
    void findByUserId() {
//        RelateCountMatrixEntity entity = dao.findByUserId("132443", "3640", "user_and_user_item_list_matrix");
//        System.out.println(JSON.toJSONString(entity));

        List<String> list = new ArrayList<>();
        list.add("1");
        list = list.stream().filter(e -> StringUtils.isNotBlank(e)).collect(Collectors.toList());
        System.out.println(JSON.toJSONString(list));
    }


}