package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "/application-test.properties")
public class StrategyListRepositoryTest {

    @Autowired
    StrategyListRepository strategyListRepository;

    @Autowired
    StrategyRepository strategyRepository;

    @Autowired
    MethodRepository methodRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    public void init() {
        entityManager.createNativeQuery("ALTER TABLE sysmetictest.strategy AUTO_INCREMENT = 1")
                .executeUpdate();
        // strategyListRepository 데이터 모두 삭제
        strategyListRepository.deleteAll();

        // 각 메서드를 호출하여 데이터 추가
        saveMember();
        saveMethod();
        saveStrategy1();
        saveStrategy2();
    }


    void saveMember() {
        // Member trader 생성
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
        // trader 저장
        memberRepository.save(trader);
    }

    void saveMethod(){
        // Method 객체 생성
        Method method = Method.builder()
                .name("Manual")
                .statusCode("MS001")
                .explanation("매뉴얼")
                .createdBy(1L)
                .modifiedBy(1L)
                .build();
        // method 저장
        methodRepository.save(method);
    }

    void saveStrategy1() {
        // Strategy 객체 생성
        Strategy s1 = Strategy.builder()
                .trader(getTrader())
                .method(getMethod())
                .statusCode("ST001")
                .name("전략1")
                .cycle('P')
                .minOperationAmount(1000000.0)
                .content("전략1 소개 내용")
                .accumProfitRate(0.0)
                .createdBy(1L)
                .modifiedBy(1L)
                .build();
        strategyRepository.saveAndFlush(s1);
    }

    void saveStrategy2() {
        Strategy s2 = Strategy.builder()
                .trader(getTrader())
                .method(getMethod())
                .statusCode("ST001")
                .name("전략2")
                .cycle('P')
                .minOperationAmount(1000000.0)
                .content("전략1 소개 내용")
                .accumProfitRate(0.0)
                .createdBy(1L)
                .modifiedBy(1L)
                .build();
        strategyRepository.saveAndFlush(s2);
    }

    Member getTrader() {
        return memberRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new NoSuchElementException("트레이더가 없습니다."));
    }

    Method getMethod(){
        return methodRepository.findAll().stream().
                findFirst().
                orElseThrow(() -> new NoSuchElementException("매매방식이 없습니다."));
    }

    @Test
    @DisplayName("전체 전략 목록 조회 테스트")
    @Transactional
    @Rollback(false)
    public void getStrategyListTest() {
        // createStrategy List로 가져오기
        // List<Strategy> strategyList = strategyListRepository.findAll().stream().toList();
        List<Strategy> strategyList = strategyListRepository.findAll();

        // strategyListRepository로 데이터베이스에 저장
        System.out.println("==========================================");
        // 요소 하나씩 출력하기 - 메서드 참조 ::
        strategyList.forEach(System.out::println);
        System.out.println("==========================================");
        assertEquals(strategyList.get(0), strategyRepository.findAll().get(0));
        assertEquals(strategyList.get(1), strategyRepository.findAll().get(1));
        // assertEquals(strategyList.get(0).getId(), strategyRepository.findAll().get(0).getId());
        // assertEquals(strategyList.get(1).getId(), strategyRepository.findAll().get(1).getId());
    }
}