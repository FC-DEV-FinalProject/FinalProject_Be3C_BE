package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyListRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "/application-test.properties")
public class StrategyListServiceTest {

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
        saveMember();
        saveMethod();
    }

    @Test
    @DisplayName("첫 번째 페이지 조회 테스트")
    @Transactional
    @Rollback(false)
    public void getFirstPageTest() {
        // before : 현재 데이터베이스 비우기
        strategyListRepository.deleteAll();
        assertTrue(strategyListRepository.findAll().isEmpty());

        // 전략 수 난수는 [1, 100]
        int randomStrategyNum = (int) (Math.random() * 100) + 1;
        // int randomStrategyNum = 8;
        System.out.println("randomStrategyNum = " + randomStrategyNum);

        // 난수만큼 순차적으로 전략 생성
        for (int i = 0; i < randomStrategyNum; i++) {
            Strategy s = Strategy.builder()
                    .trader(getTrader())
                    .method(getMethod())
                    .statusCode("ST001")
                    .name("전략" + (i + 1))
                    .cycle('P')
                    .minOperationAmount(100.0)
                    .content("전략" + (i + 1) + " 소개 내용")
                    .accumProfitRate(Math.random() * 100)
                    .createdBy(Long.valueOf(randomStrategyNum))
                    .modifiedBy(Long.valueOf(randomStrategyNum))
                    .build();

            // 저장할 때는 하나씩 등록하니까 StrategyRepository 사용해서 하나씩 등록
            em.persist(s);
            em.flush();
            em.clear();
        }

        // 전략 첫 번째 페이지 가져오기
        Page<Strategy> firstPage = strategyListService.findStrategyPage(0);
        assertNotNull(firstPage);
        assertEquals(firstPage.getSize(), 10);
        assertTrue(firstPage.hasContent());

        // 첫 번째 페이지 전략 반복 검증
        for (Strategy s : firstPage) {
            assertNotNull(s.getId());
            assertNotNull(s.getTrader());
            assertNotNull(s.getMethod());
            assertEquals(s.getStatusCode(), "ST001");
            assertNotNull(s.getName());
            assertEquals(s.getCycle(), 'P');
            assertEquals(s.getMinOperationAmount(), 100.0);
            assertNotNull(s.getAccumProfitRate());
            assertEquals(s.getCreatedBy(), Long.valueOf(randomStrategyNum));
            assertEquals(s.getModifiedBy(), Long.valueOf(randomStrategyNum));
        }
        // 첫 페이지 첫 번째 값이 제일 큰 accumProfitRate 가져야 함
        for (Strategy s : firstPage){
            assertTrue(firstPage.getContent().get(0).getAccumProfitRate() >= s.getAccumProfitRate());
            System.out.println("s.getAccumProfitRate() = " + s.getAccumProfitRate());
        }
    }


    @Test
    @DisplayName("전략 목록 전체 페이지 수")
    public void getAllStrategyPageNum() {
        // before : 현재 데이터베이스 비우기
        strategyListRepository.deleteAll();
        assertTrue(strategyListRepository.findAll().isEmpty());

        // 전략 수 난수는 [1, 100]
        int randomStrategyNum = (int) (Math.random() * 100) + 1;
        // int randomStrategyNum = 9;
        System.out.println("randomStrategyNum = " + randomStrategyNum);

        // 난수만큼 순차적으로 전략 생성
        for (int i = 0; i < randomStrategyNum; i++) {
            Strategy s = Strategy.builder()
                    .trader(getTrader())
                    .method(getMethod())
                    .statusCode("ST001")
                    .name("전략" + (i + 1))
                    .cycle('P')
                    .minOperationAmount(100.0)
                    .content("전략" + (i + 1) + " 소개 내용")
                    .accumProfitRate(Math.random() * 100)
                    .createdBy(Long.valueOf(randomStrategyNum))
                    .modifiedBy(Long.valueOf(randomStrategyNum))
                    .build();

            // 저장할 때는 하나씩 등록하니까 StrategyRepository 사용해서 하나씩 등록
            em.persist(s);
            em.flush();
            em.clear();
        }
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

    Method getMethod(){
        return methodRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new NoSuchElementException("매매방식이 없습니다."));
    }

}