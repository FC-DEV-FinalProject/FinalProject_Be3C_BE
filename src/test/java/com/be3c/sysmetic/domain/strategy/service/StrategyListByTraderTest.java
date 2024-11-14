package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.strategy.dto.StrategyListByTraderDto;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyListRepository;
import com.be3c.sysmetic.global.common.response.PageResponse;
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
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "/application-test.properties")
public class StrategyListByTraderTest {


    @Autowired
    StrategyListService strategyListService;

    // saveMember(), saveMethod(), deleteAll() 위함
    @Autowired
    StrategyListRepository strategyListRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MethodRepository methodRepository;
    @Autowired
    EntityManager em;

    @BeforeEach
    public void init() {
        em.createNativeQuery("ALTER TABLE sysmetictest.strategy AUTO_INCREMENT = 1")
                .executeUpdate();
        // strategyListRepository 데이터 모두 삭제
        strategyListRepository.deleteAll();
        saveMember("홍길동");
        saveMethod();
    }


    @Test
    @DisplayName("트레이더별 전략 목록")
    @Transactional
    @Rollback(false)
    public void findStrategiesByTraderTest() {
        // before : 현재 데이터베이스 비우기
        strategyListRepository.deleteAll();
        assertTrue(strategyListRepository.findAll().isEmpty());

        // 전략 수 난수는 [1, 100]
        // int randomStrategyNum = (int) (Math.random() * 100 + 1);
        int randomStrategyNum = 11;
        // System.out.println("randomStrategyNum = " + randomStrategyNum);

        saveMember("나는부자");

        // 트레이더 수만큼 전략 생성 후 저장
        for (int i=0; i < randomStrategyNum; i++) {
            Strategy s = Strategy.builder()
                    .trader(getTrader("나는부자"))
                    .method(getMethod())
                    .statusCode("ST001")
                    .name("전략" + (i+1))
                    .cycle('P')
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) (Math.random() * 100))
                    .accumProfitLossRate(Math.random() * 100)
                    .build();
            em.persist(s);
            em.flush();
            em.clear();
        }

        PageResponse<StrategyListByTraderDto> page = strategyListService.findStrategiesByTrader(getTrader("나는부자").getId(), 0);
        assertNotNull(page);
        assertEquals(page.getPageSize(), 10);
        double maxProfitRate = page.getContent().get(0).getAccumProfitLossRate();

        for (int i=0; i < page.getContent().size(); i++){
            assertNotNull(page.getContent().get(i));
            assertEquals(page.getContent().get(i).getTraderNickname(), "나는부자");
            assertTrue(page.getContent().get(i).getAccumProfitLossRate() <= maxProfitRate);
        }
    }


    @Test
    @DisplayName("트레이더의 전략 목록 없음")
    @Transactional
    @Rollback(false)
    public void failFindStrategiesByTraderTest() {
        // before : 현재 데이터베이스 비우기
        strategyListRepository.deleteAll();
        memberRepository.deleteAll();
        assertTrue(strategyListRepository.findAll().isEmpty());

        int randomStrategyNum = 11;

        saveMember("여의도전략가");
        saveMember("나는부자");

        // 트레이더 수만큼 전략 생성 후 저장
        for (int i=0; i < randomStrategyNum; i++) {
            Strategy s = Strategy.builder()
                    .trader(getTrader("나는부자"))
                    .method(getMethod())
                    .statusCode("ST001")
                    .name("전략" + (i+1))
                    .cycle('P')
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) (Math.random() * 100))
                    .accumProfitLossRate(Math.random() * 100)
                    .build();
            em.persist(s);
            em.flush();
            em.clear();
        }

        PageResponse<StrategyListByTraderDto> page = strategyListService.findStrategiesByTrader(getTrader("여의도전략가").getId(), 0);
        assertNotNull(page);
        assertEquals(page.getPageSize(), 10);
    }


    void saveMember(String nickname) {
        // Member trader 생성
        Member trader = Member.builder()
                .roleCode("trader")
                .email("trader1@gmail.com")
                .password("1234")
                .name("김이박")
                .nickname(nickname)
                .phoneNumber("01012341234")
                .usingStatusCode("using status code")
                .totalFollow(0)
                .totalStrategyCount(0)
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
                .createdBy(1L)
                .modifiedBy(1L)
                .build();
        // method 저장
        methodRepository.save(method);
    }

    Member getTrader() {
        return memberRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new NoSuchElementException("트레이더가 없습니다."));
    }

    Member getTrader(String nickname) {
        return memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("해당 트레이더가 없습니다."));
    }

    Method getMethod(){
        return methodRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new NoSuchElementException("매매방식이 없습니다."));
    }
}
