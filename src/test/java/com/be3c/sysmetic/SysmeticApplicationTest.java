package com.be3c.sysmetic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
class SysmeticApplicationTest {
    public static void main(String[] args) {
        SpringApplication.run(SysmeticApplication.class, args);
    }
}