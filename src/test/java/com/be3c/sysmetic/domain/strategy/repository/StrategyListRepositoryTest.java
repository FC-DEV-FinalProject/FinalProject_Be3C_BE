package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

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
    EntityManager em;

    @BeforeEach
    public void init() {
        em.createNativeQuery("ALTER TABLE sysmetictest.strategy AUTO_INCREMENT = 1")
                .executeUpdate();
        // strategyListRepository 데이터 모두 삭제
        strategyListRepository.deleteAll();

        // 각 메서드를 호출하여 데이터 추가
        saveMember();
        saveMethod("Manual");
    }

    @Test
    @DisplayName("전략 목록 첫 번째 페이지 조회 테스트 - 수익률순")
    @Transactional
    @Rollback(false)
    public void strategyFirstPageTest() {
        // before : 현재 데이터베이스 비우기
        strategyListRepository.deleteAll();
        assertTrue(strategyListRepository.findAll().isEmpty());

        // 전략 수 난수는 [1, 100]
        // int randomStrategyNum = (int) (Math.random() * 100) + 1;
        int randomStrategyNum = 9;
        System.out.println(randomStrategyNum);

        // 난수만큼 순차적으로 전략 생성
        for (int i = 0; i < randomStrategyNum; i++) {
            Strategy s = Strategy.builder()
                    .trader(getTrader())
                    .method(getMethod())
                    .statusCode("ST001")
                    .name("전략" + (i+1))
                    .cycle('P')
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) (Math.random() * 100))
                    .accumProfitLossRate(Math.random() * 100)
                    .build();
            strategyRepository.saveAndFlush(s);        // 저장할 때는 하나씩 등록하니까 StrategyRepository 사용해서 하나씩 등록
        }

        // 전략 페이지 하나 가져오기
        Pageable pageable = strategyListRepository.getPageable(0, "accumProfitLossRate");
        Page<Strategy> spage = strategyListRepository.findAllByStatusCode("ST001", pageable);

        assertNotNull(spage);

        for (Strategy s : spage) {
            assertNotNull(s.getId());
            assertNotNull(s.getTrader());
            assertNotNull(s.getMethod());
            assertEquals(s.getStatusCode(), "ST001");
            assertNotNull(s.getName());
            assertEquals(s.getCycle(), 'P');
            assertNotNull(s.getAccumProfitLossRate());
        }

        // 페이지 크기 확인
        assertEquals(10, spage.getSize());

        // 전체 페이지 개수 검증
        int totalPage = (int) Math.ceil((double) randomStrategyNum / 10);
        assertEquals(totalPage, spage.getTotalPages());
        System.out.println("totalPage = " + totalPage);
        System.out.println("spage.getTotalPages() = " + spage.getTotalPages());

        // 수익률순 - spage의 첫 번째 데이터
        Strategy firstStrategy = spage.getContent().get(0);
        for (Strategy s : spage.getContent()) {
            assertTrue(firstStrategy.getAccumProfitLossRate() >= s.getAccumProfitLossRate(), "수익률이 내림차순으로 정렬되지 않음.");
            System.out.println("s.getAccumProfitLossRate() = " + s.getAccumProfitLossRate());
        }
    }


    @Test
    @DisplayName("전략 목록 전체 페이지 조회")
    @Transactional
    @Rollback(false)
    public void allStrategyPageTest() {
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
                    .name("전략" + (i+1))
                    .cycle('P')
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) (Math.random() * 100))
                    .accumProfitLossRate(Math.random() * 100)
                    .build();
            strategyRepository.saveAndFlush(s);        // 저장할 때는 하나씩 등록하니까 StrategyRepository 사용해서 하나씩 등록
        }

        // 전체 페이지 계산
        int actualTotalPage = (int) Math.ceil(strategyListRepository.countByStatusCode("ST001") / 10.0);
        int expectedTotalPage = (int) (Math.ceil(randomStrategyNum / 10.0));
        assertEquals(actualTotalPage, expectedTotalPage);

        // 전체 페이지 조회
        for (int i=0; i < actualTotalPage; i++) {
            Pageable pageable = strategyListRepository.getPageable(i, "accumProfitLossRate");
            Page<Strategy> page = strategyListRepository.findAllByStatusCode("ST001", pageable);
            assertTrue(page.hasContent());
            if (i+1 != actualTotalPage) assertTrue(page.hasNext());
            assertTrue(page.getSort().isSorted());

            // 해당 페이지의 첫 번째 전략
            Strategy firstStrategyOfPage = page.getContent().get(0);
            //
            for (Strategy s : page.getContent()) {
                assertTrue(firstStrategyOfPage.getAccumProfitLossRate() >= s.getAccumProfitLossRate(), "수익률이 내림차순으로 정렬되지 않음.");
            }
        }
    }


    @Test
    @DisplayName("전략 목록 전체 페이지 수")
    @Transactional
    @Rollback(false)
    public void allStrategyPageNumberTest() {
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
                    .name("전략" + (i+1))
                    .cycle('P')
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) (Math.random() * 100))
                    .accumProfitLossRate(Math.random() * 100)
                    .build();
            strategyRepository.saveAndFlush(s);        // 저장할 때는 하나씩 등록하니까 StrategyRepository 사용해서 하나씩 등록
        }

        // 전체 페이지 수 구하기
        int actualTotalPage = (int) Math.ceil(strategyListRepository.countByStatusCode("ST001") / 10.0);
        int expectedTotalPage = (int) Math.ceil(randomStrategyNum / 10.0);
        // 예상 값과 실제 값이 일치하는지 검증
        assertEquals(expectedTotalPage, actualTotalPage);
        System.out.println("expectedTotalPage = " + expectedTotalPage);
        System.out.println("actualTotalPage = " + actualTotalPage);
    }

    @Test
    @DisplayName("현재 페이지가 전체 페이지 보다 크면 안됨")
    @Transactional
    @Rollback(false)
    public void curPageNumTest() {
        // before : 현재 데이터베이스 비우기
        strategyListRepository.deleteAll();
        assertTrue(strategyListRepository.findAll().isEmpty());

        // 전략 수 난수는 [1, 100]
        int randomStrategyNum = (int) (Math.random() * 100) + 1;
        // int randomStrategyNum = 9;
        System.out.println("randomStrategyNum = " + randomStrategyNum);

        // 난수만큼 전략 생성 후 데이터베이스에 저장
        for (int i=0; i < randomStrategyNum; i++){
            Strategy s = Strategy.builder()
                    .trader(getTrader())
                    .method(getMethod())
                    .statusCode("ST001")
                    .name("전략" + (i+1))
                    .cycle('P')
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) (Math.random() * 100))
                    .accumProfitLossRate(Math.random() * 100)
                    .build();
            strategyRepository.saveAndFlush(s);        // 저장할 때는 하나씩 등록하니까 StrategyRepository 사용해서 하나씩 등록
        }

        // 전체 페이지 중 난수로 현재 페이지 curPage 생성
        int randomCurPage = (int) (Math.random() * randomStrategyNum) / 10 + 1;
        // 전체 페이지
        int actualTotalPage = (int) Math.ceil(strategyListRepository.countByStatusCode("ST001") / 10.0);

        // 현재 페이지가 전체 페이지보다 크면 안됨
        System.out.println("randomCurPage = " + randomCurPage);
        System.out.println("actualTotalPage = " + actualTotalPage);

        assertTrue(randomCurPage <= actualTotalPage);
    }


    @Test
    @DisplayName("트레이더 별 전략 목록")
    @Transactional
    @Rollback(false)
    public void findPageByTraderNicknameTest() {
        // before : 현재 데이터베이스 비우기
        strategyListRepository.deleteAll();
        memberRepository.deleteAll();
        assertTrue(strategyListRepository.findAll().isEmpty());

        // 난수는 [1, 50]
        // int randomNum = (int) (Math.random() * 50) + 1;
        int randomNum = 50;
        System.out.println("randomNum = " + randomNum);

        saveMember("강남아빠");
        saveMember("강남부자");

        // 트레이더 수만큼 전략 생성 후 저장
        for (int i=0; i < 25; i++) {
            Strategy s = Strategy.builder()
                    .trader(getTrader("강남아빠"))
                    .method(getMethod())
                    .statusCode("ST001")
                    .name("전략" + (i+1))
                    .cycle('P')
                    .minOperationAmount(100.0)
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) ((Math.random() * 100) + 1))
                    .accumProfitRate(Math.random() * 100)
                    .createdBy((long) randomNum)
                    .modifiedBy((long) randomNum)
                    .build();
            strategyRepository.saveAndFlush(s);        // 저장할 때는 하나씩 등록하니까 StrategyRepository 사용해서 하나씩 등록
        }

        for (int i=0; i < 25; i++) {
            Strategy s = Strategy.builder()
                    .trader(getTrader("강남부자"))
                    .method(getMethod())
                    .statusCode("ST001")
                    .name("전략" + (i+1))
                    .cycle('P')
                    .minOperationAmount(100.0)
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) ((Math.random() * 100) + 1))
                    .accumProfitRate(Math.random() * 100)
                    .createdBy((long) randomNum)
                    .modifiedBy((long) randomNum)
                    .build();
            strategyRepository.saveAndFlush(s);        // 저장할 때는 하나씩 등록하니까 StrategyRepository 사용해서 하나씩 등록

        }

        Page<Strategy> page;

        // 트레이더 하나에 대한 전략 목록
        int pageNum = 0;
        do {
            // pageable 객체를 현재 pageNum에 맞게 생성
            Pageable pageable = PageRequest.of(pageNum, 10, Sort.by(Sort.Order.desc("accumProfitRate")));
            page = strategyListRepository.findByTrader(getTrader("강남아빠"), pageable);

            assertTrue(page.hasContent());
            assertTrue(page.getSort().isSorted());

            double maxProfitRate = page.getContent().get(0).getAccumProfitRate();
            for (Strategy s : page) {
                assertEquals(s.getTrader().getNickname(), "강남아빠");
                assertTrue(s.getAccumProfitRate() <= maxProfitRate);
                System.out.println("id = " + s.getId() + ", nickname = " + s.getTrader().getNickname() + ", accumProfitRate = " + s.getAccumProfitRate());
            }

            pageNum++; // 다음 페이지로 이동하기 위해 pageNum을 증가
        } while (page.hasNext());
    }


    void saveMember() {
        // Member trader 생성
        Member trader = Member.builder()
                .roleCode("trader")
                .email("trader1@gmail.com")
                .password("1234")
                .name("홍길동")
                .nickname("김아무개")
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



    void saveMember(String nickname) {
        // Member trader 생성
        Member trader = Member.builder()
                .roleCode("trader")
                .email("trader1@gmail.com")
                .password("1234")
                .name("홍길동")
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

    void saveMethod(String name){
        // Method 객체 생성
        Method method = Method.builder()
                // .name("Manual")
                .name(name)
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
                // .orElseGet(this::getTrader);
    }

    Method getMethod(){
        return methodRepository.findAll().stream().
                findFirst().
                orElseThrow(() -> new NoSuchElementException("매매방식이 없습니다."));
    }
}