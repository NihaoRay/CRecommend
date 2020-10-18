package com.marsdl.recommand.remmand;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserCFTest {

    @Resource
    private UserCF userCF;

    @Test
    void createUserAndUserItemListMapMatrix() {
        userCF.createUserAndUserItemListMapMatrix();
    }
}