package com.marsdl.recommand.dao;

import com.marsdl.recommand.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class Dao {

    @Resource
    private MongoTemplate mongoTemplate;

    public boolean savaMap(Map<String, List<String>> stringListMap, Class<? extends BaseEntity> clazz,
                           final String collectionName) {
        try {
            Constructor constructor = clazz.getConstructor(String.class, List.class);
            for (Map.Entry<String, List<String>> entry : stringListMap.entrySet()) {
                Object o = constructor.newInstance(entry.getKey(), entry.getValue());
                mongoTemplate.save(o, collectionName);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //mongodb查询所有的数据
    public <T> List<T> findByCursorId(String id, Class<T> t, final String collectionName) {
        Query query = new Query();
        if (StringUtils.isNotBlank(id)) {
            query.addCriteria(Criteria.where("_id").gt(new ObjectId(id)));
        }
        if (StringUtils.isBlank(id)) {
            query.addCriteria(Criteria.where("_id").gt(new ObjectId(new Date(0))));
        }
        query.limit(20);
        return mongoTemplate.find(query, t, collectionName);
    }

    public long count(final String collectionName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").gt(new ObjectId(new Date(0))));
        return mongoTemplate.count(query, collectionName);
    }

    public UserItemList findUserItemListById(String userId, final String collectionName) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        Query query = new Query();
        if (StringUtils.isNotBlank(userId)) {
            query.addCriteria(Criteria.where("userId").is(userId));
        }
        return mongoTemplate.findOne(query, UserItemList.class, collectionName);
    }

    public ItemUserList findItemUserListById(String itemId, final String collectionName) {
        if (StringUtils.isBlank(itemId)) {
            return null;
        }
        Query query = new Query();
        if (StringUtils.isNotBlank(itemId)) {
            query.addCriteria(Criteria.where("itemId").is(itemId));
        }
        return mongoTemplate.findOne(query, ItemUserList.class, collectionName);
    }

    public void update(String userId, Map<String, Integer> userItemListMap, final String collectionName) {
        Query query = new Query();
        if (StringUtils.isNotBlank(userId)) {
            query.addCriteria(Criteria.where("userId").is(userId));
        }
        Update update = new Update();
        update.set("userItemListMap", userItemListMap);
        mongoTemplate.upsert(query, update, UserAndUserItemList.class, collectionName);
    }

    public void updateRelateCountMatrix(String userId, Map<String, Integer> userItemListMap, final String collectionName) {
        List<RelateCountMatrixEntity> list = new ArrayList<>();
        for (Map.Entry<String, Integer> userItemListEntry : userItemListMap.entrySet()) {
            list.add(new RelateCountMatrixEntity(userId, userItemListEntry.getKey(), userItemListEntry.getValue()));
        }
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        mongoTemplate.insert(list, collectionName);
    }

//    public void saveRelateCount(RelateCountMatrixEntity entity, final String collectionName) {
//        mongoTemplate.save(entity, collectionName);
//    }

    public List<RelateCountMatrixEntity> findRelateCountMatrixByAxis(String axis, final String collectionName) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(Criteria.where("xAxis").is(axis), Criteria.where("yAxis").is(axis)));
        return mongoTemplate.find(query, RelateCountMatrixEntity.class, collectionName);
    }

    public <T> void saveEntity(T entity, final String collectionName) {
        mongoTemplate.save(entity, collectionName);
    }

}
