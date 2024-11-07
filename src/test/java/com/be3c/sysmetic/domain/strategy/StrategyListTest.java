package com.be3c.sysmetic.domain.strategy;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "/application-test.properties")
public class StrategyListTest {

    @Autowired
    EntityManager em;

    @Test
    @Commit
    @DisplayName("전략 영속성 테스트")
    public void persistStrategyTest() {
        // Method 객체 생성
        Method m = Method.builder()
                .name("Manual")
                .statusCode("MS001")
                .explanation("매뉴얼")
                .createdBy(1L)
                .modifiedBy(1L)
                .build();


        // Method를 영속성 상태로
        em.persist(m);
        // EntityManager 반영
        em.flush();
        // EntityManger clear
        em.clear();

        // 영속 상태 Method를 em을 이용해 가져오기
        Method findMethod = em.find(Method.class, m.getId());
        assertNotNull(findMethod);
        System.out.println(findMethod);
        assertEquals(findMethod.getId(), m.getId());

        // Member trader 객체 생성
        Member trader = Member.builder()
                .roleCode("trader")
                .email("trader1@gmail.com")
                .password("1234")
                .name("홍길동")
                .nickname("gildong")
                .phoneNumber("01012341234")
                .usingStatusCode("using status code")
                .totalFollow(0)
                .receiveInfoConsent("Yes")
                .infoConsentDate(LocalDateTime.now())
                .receiveMarketingConsent("NO")
                .marketingConsentDate(LocalDateTime.now())
                .createdBy(1L)
                .createdDate(LocalDateTime.now())
                .modifiedBy(1L)
                .modifiedDate(LocalDateTime.now())
                .build();

        // trader 영속 상태로
        em.persist(trader);
        em.flush();
        em.clear();

        // Strategy 객체 생성
        Strategy s1 = Strategy.builder()
                .trader(em.find(Member.class, trader.getId()))
                .method(em.find(Method.class, m.getId()))
                .statusCode("ST001")
                .name("전략1")
                .cycle('P')
                .minOperationAmount(1000000.0)
                .content("전략1 소개 내용")
                .accumProfitRate(0.0)
                .createdBy(1L)
                .modifiedBy(1L)
                .build();

        // s1 영속 상태로
        em.persist(s1);
        em.flush();
        em.clear();

        // Strategy s2 = Strategy.builder()
        //         .trader(em.find(Member.class, trader.getId()))
        //         .method(em.find(Method.class, m.getId()))
        //         .statusCode("ST001")
        //         .name("전략2")
        //         .cycle('P')
        //         .minOperationAmount(1000000.0)
        //         .content("전략1 소개 내용")
        //         .accumProfitRate(0.0)
        //         .createdBy(1L)
        //         .modifiedBy(1L)
        //         .build();

        // s2 영속 상태로
        // em.persist(s2);
        // em.flush();
        // em.clear();

        // em으로 가져오기
        Strategy findStrategy = em.find(Strategy.class, s1.getId());
        assertNotNull(findStrategy);
        assertEquals(findStrategy.getId(), s1.getId());
        assertEquals(findStrategy.getName(), s1.getName());
        assertEquals(findStrategy.getTrader().getId(), s1.getTrader().getId());
        assertEquals(findStrategy.getMethod().getId(), s1.getMethod().getId());
    }
}
