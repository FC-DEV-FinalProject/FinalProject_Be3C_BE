package com.be3c.sysmetic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = "/application-test.properties")
@SpringBootTest
class SysmeticApplicationTests {

	EntityManagerFactory emf = Persistence.createEntityManagerFactory("sysmetic_test");
	EntityManager em = emf.createEntityManager();
	EntityTransaction tx = em.getTransaction();

	@Test
	void contextLoads() {
	}
}