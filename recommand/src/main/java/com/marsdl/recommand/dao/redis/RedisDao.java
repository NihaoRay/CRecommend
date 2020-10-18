package com.marsdl.recommand.dao.redis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;


@Repository
public class RedisDao {
    private static final Logger log = LogManager.getLogger(RedisDao.class);

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public boolean saveStrValue(String userId, String itemUserId) {
        try {
            if (!hasKey(userId, itemUserId)) {
                redisTemplate.opsForValue().set(keyGenerate(userId, itemUserId), "1");
            }
            return true;
        } catch (Exception e) {
            log.error("{} 存入redis失败 {}", keyGenerate(userId, itemUserId), e.getMessage());
            return false;
        }
    }

    public boolean hasKey(String userId, String itemUserId) {
        boolean isHas = redisTemplate.hasKey(keyGenerate(itemUserId, userId));
        if (!isHas) {
            isHas = redisTemplate.hasKey(keyGenerate(userId, itemUserId));
        }
        return isHas;
    }

    private static String keyGenerate(String userId, String itemUserId) {
        return userId + "_" + itemUserId;
    }

}
