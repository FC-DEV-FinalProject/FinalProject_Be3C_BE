package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.strategy.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.StrategyListDto;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
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
        saveMember("홍길동");
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
        System.out.println("randomStrategyNum = " + randomStrategyNum);

        // 난수만큼 순차적으로 전략 생성
        for (int i = 0; i < randomStrategyNum; i++) {
            Strategy s = Strategy.builder()
                    .trader(getTrader())
                    .method(getMethod())
                    .statusCode("ST001")
                    .name("전략" + (i + 1))
                    .cycle('P')
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) (Math.random() * 100))
                    .accumProfitLossRate(Math.random() * 100)
                    .build();
            em.persist(s);
            em.flush();
            em.clear();
        }

        // 전략 첫 번째 페이지 가져오기
        PageResponse<StrategyListDto> firstPage = strategyListService.findStrategyPage(0);
        assertNotNull(firstPage);
        assertEquals(10, firstPage.getPageSize());
        assertNotNull(firstPage.getContent());

        // 첫 번째 페이지 전략 반복 검증
        for (int i=0; i < firstPage.getContent().size(); i++) {
            assertNotNull(firstPage.getContent().get(i).getName());
            assertNotNull(firstPage.getContent().get(i).getStock());
            assertNotNull(firstPage.getContent().get(i).getTraderNickname());
            assertNotNull(firstPage.getContent().get(i).getCycle());
            assertNotNull(firstPage.getContent().get(i).getAccumProfitLossRate());
        }

        // 첫 페이지 첫 번째 값이 제일 큰 accumProfitLossRate 가져야 함
        for (int i=0; i < 10; i++) {
            assertTrue(firstPage.getContent().get(0).getAccumProfitLossRate() >= firstPage.getContent().get(i).getAccumProfitLossRate());
            System.out.println("dto.getAccumProfitLossRate() = " + firstPage.getContent().get(i).getAccumProfitLossRate());
        }
    }


    @Test
    @DisplayName("전략 목록 전체 페이지 수")
    @Transactional
    @Rollback(false)
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
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) (Math.random() * 100))
                    .accumProfitLossRate(Math.random() * 100)
                    .build();

            // 저장할 때는 하나씩 등록하니까 StrategyRepository 사용해서 하나씩 등록
            em.persist(s);
            em.flush();
            em.clear();
        }

        // 전체 페이지 수 확인
        int expectedTotalPage = (int) Math.ceil(randomStrategyNum / 10.0);
        int actualTotalPage = strategyListService.getTotalPageNumber("ST001", 10);
        System.out.println("expectedTotalPage = " + expectedTotalPage);
        System.out.println("actualTotalPage = " + actualTotalPage);
    }


    @Test
    @DisplayName("특정 페이지 조회")
    @Transactional
    @Rollback(false)
    public void getSelectedPageTest() {
        // before : 현재 데이터베이스 비우기
        strategyListRepository.deleteAll();
        assertTrue(strategyListRepository.findAll().isEmpty());

        // 전략 수 난수는 [1, 100]
        int randomStrategyNum = (int) (Math.random() * 100 + 1);
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
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) (Math.random() * 100))
                    .accumProfitLossRate(Math.random() * 100)
                    .build();

            // 저장할 때는 하나씩 등록하니까 StrategyRepository 사용해서 하나씩 등록
            em.persist(s);
            em.flush();
            em.clear();
        }

        // 전체 페이지 계산
        int totalPage = (int) Math.ceil(randomStrategyNum / 10.0);
        // 랜덤 페이지 선택
        int randomPage = (int) (Math.random() * totalPage);
        System.out.println("randomPage = " + randomPage);

        // 특정 페이지 가져오기
        PageResponse<StrategyListDto> selectPage = strategyListService.findStrategyPage(randomPage);
        assertFalse(selectPage.getContent().isEmpty(), "선택한 페이지에 데이터 없음.");
        double maxProfitRate = selectPage.getContent().get(0).getAccumProfitLossRate();

        // 가져온 페이지도 수익률 순으로 정렬되어야 함
        for (int i=0; i < selectPage.getContent().size(); i++) {
            assertTrue(maxProfitRate >= selectPage.getContent().get(i).getAccumProfitLossRate());
            System.out.println("s.getAccumProfitLossRate() = " + selectPage.getContent().get(i).getAccumProfitLossRate());
        }
    }


    @Test
    @DisplayName("전략 목록 전체 페이지 조회")
    @Transactional
    @Rollback(false)
    public void getAllStrategyPages() {
        // before : 현재 데이터베이스 비우기
        strategyListRepository.deleteAll();
        assertTrue(strategyListRepository.findAll().isEmpty());

        // 전략 수 난수는 [1, 100]
        int randomStrategyNum = (int) (Math.random() * 100 + 1);
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
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) (Math.random() * 100))
                    .accumProfitLossRate(Math.random() * 100)
                    .build();

            // 저장할 때는 하나씩 등록하니까 StrategyRepository 사용해서 하나씩 등록
            em.persist(s);
            em.flush();
            em.clear();
        }

        // 전체 페이지 계산
        int expectedTotalPage = (int) Math.ceil(randomStrategyNum / 10.0);
        int actualTotalPage = strategyListService.getTotalPageNumber("ST001", 10);
        assertEquals(expectedTotalPage, actualTotalPage);

        for (int i=0; i < actualTotalPage; i++) {
            PageResponse<StrategyListDto> page = strategyListService.findStrategyPage(i);
            assertNotNull(page);

            for (int j=0; j < page.getContent().size(); j++) {
                assertTrue(page.getContent().get(0).getAccumProfitLossRate() >= page.getContent().get(j).getAccumProfitLossRate());
                System.out.println("accumProfitLossRate = " + page.getContent().get(j).getAccumProfitLossRate());
            }
            System.out.println("=====================");
        }
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