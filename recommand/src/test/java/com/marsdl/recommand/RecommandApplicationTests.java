package com.marsdl.recommand;

import com.marsdl.recommand.dao.Dao;
import com.marsdl.recommand.dao.redis.RedisDao;
import com.marsdl.recommand.entity.CustomRatingEntity;
import com.marsdl.recommand.io.ReadCsv;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class RecommandApplicationTests {

    @Resource
    private Dao dao;

    @Resource
    private ReadCsv readCsv;

    @Resource
    private RedisDao redisDao;

    @Test
    void contextLoads() {
        CustomRatingEntity entity = new CustomRatingEntity();
        entity.setMovieId("123");
        entity.setRating(12d);
        entity.setUserId("321");


        System.out.println("---------------");
    }

    @Test
    void readcsv() throws Exception {
//        String filePath = "C:\\Users\\chenrui\\Desktop\\test.csv";
        String filePath = "C:\\Users\\chenrui\\Desktop\\70w.csv";
//        String filePath = "C:\\Users\\chenrui\\Desktop\\10million_ratings.csv";
//        String filePath = "C:\\Users\\chenrui\\Desktop\\10million_ratings.csv";
//        String filePath = "C:\\Users\\chenrui\\Desktop\\170w.csv";
        readCsv.readWithCsvBeanReader(filePath);


    }


}
