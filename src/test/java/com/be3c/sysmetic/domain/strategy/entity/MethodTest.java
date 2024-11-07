package com.be3c.sysmetic.domain.strategy.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "/application-test.properties")
// @Commit     // 트랜잭션을 커밋하여 데이터가 남도록 함
class MethodTest {

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("종목 DB 저장 테스트")
    @Commit
    public void methodPersistTest() {
        // Method 객체 생성
        Method method = Method.builder()
                .name("Manual")
                .statusCode("MS001")
                .explanation("매뉴얼")
                .createdBy(1L)
                .modifiedBy(1L)
                .build();


        // Method를 영속성 상태로
        em.persist(method);
        // EntityManager 반영
        em.flush();
        // EntityManger clear
        em.clear();

        // 영속 상태 Method를 em을 이용해 가져오기
        Method findMethod = em.find(Method.class, method.getId());
        assertNotNull(findMethod);
        System.out.println(findMethod);
        assertEquals(findMethod.getId(), method.getId());
    }
}