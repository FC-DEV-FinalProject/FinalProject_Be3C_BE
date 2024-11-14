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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "/application-test.properties")
public class StrategyListByTraderRepositoryTest {

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
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) ((Math.random() * 100) + 1))
                    .accumProfitLossRate(Math.random() * 100)
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
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) ((Math.random() * 100) + 1))
                    .accumProfitLossRate(Math.random() * 100)
                    .build();
            strategyRepository.saveAndFlush(s);        // 저장할 때는 하나씩 등록하니까 StrategyRepository 사용해서 하나씩 등록

        }

        Page<Strategy> page;

        // 트레이더 하나에 대한 전략 목록
        int pageNum = 0;
        do {
            // pageable 객체를 현재 pageNum에 맞게 생성
            Pageable pageable = PageRequest.of(pageNum, 10, Sort.by(Sort.Order.desc("accumProfitLossRate")));
            page = strategyListRepository.findAllByTraderAndStatusCode(getTrader("강남아빠"), "ST001", pageable);

            assertTrue(page.hasContent());
            assertTrue(page.getSort().isSorted());

            double maxProfitRate = page.getContent().get(0).getAccumProfitLossRate();
            for (Strategy s : page) {
                assertEquals(s.getTrader().getNickname(), "강남아빠");
                assertTrue(s.getAccumProfitLossRate() <= maxProfitRate);
                System.out.println("id = " + s.getId() + ", nickname = " + s.getTrader().getNickname() + ", accumProfitLossRate = " + s.getAccumProfitLossRate());
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
                .methodCreatedDate(LocalDateTime.now())
                .createdBy(1L)
                .createdDate(LocalDateTime.now())
                .modifiedBy(1L)
                .modifiedDate(LocalDateTime.now())
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
