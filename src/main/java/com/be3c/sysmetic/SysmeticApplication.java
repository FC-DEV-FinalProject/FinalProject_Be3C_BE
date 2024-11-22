package com.be3c.sysmetic;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SysmeticApplication {

	public static void main(String[] args) {
		SpringApplication.run(SysmeticApplication.class, args);
	}


	// QueryDSL에서 사용함!
	@Bean
	JPAQueryFactory jpaQueryFactory(EntityManager em){
		return new JPAQueryFactory(em);
	}
}