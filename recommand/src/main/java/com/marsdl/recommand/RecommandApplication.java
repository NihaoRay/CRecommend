package com.marsdl.recommand;

import com.marsdl.recommand.io.ReadCsv;
import com.marsdl.recommand.remmand.ItemCF;
import com.marsdl.recommand.remmand.UserCF;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class RecommandApplication implements CommandLineRunner {

    @Resource
    private UserCF userCF;
    @Resource
    private ItemCF itemCF;
    @Resource
    private ReadCsv readCsv;

    public static void main(String[] args) {
        SpringApplication.run(RecommandApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
		String filePath = "C:\\Users\\chenrui\\Desktop\\test.csv";
//        String filePath = "C:\\Users\\chenrui\\Desktop\\10million_ratings.csv";
//        String filePath = "C:\\Users\\chenrui\\Desktop\\10million_ratings.csv";
//        String filePath = "C:\\Users\\chenrui\\Desktop\\70w.csv";
//		readCsv.readWithCsvBeanReader(filePath);
//        userCF.createMatrix();
//        userCF.createItemUserListCountMap();
//        userCF.createMatrix();
        itemCF.createMatrix();
    }
}
