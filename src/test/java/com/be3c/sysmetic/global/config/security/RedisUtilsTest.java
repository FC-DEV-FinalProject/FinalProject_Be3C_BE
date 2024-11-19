package com.be3c.sysmetic.global.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisUtilsTest {

    @Autowired
    RedisUtils redisUtils;

    @Test
    void saveToken() {
    }

    @Test
    void getRefreshToken() {
    }

    @Test
    void deleteToken() {
    }

    @Test
    void saveEmailAuthCodeWithExpireTime() {
    }

    @Test
    void getEmailAuthCode() {
        redisUtils.saveEmailAuthCodeWithExpireTime("test", "authCode", 60 * 60 * 1000L);

        System.out.println("조회하는 메서드 결과 = "+redisUtils.getEmailAuthCode("test"));
    }

    @Test
    void deleteEmailAuthCode() {
    }
}