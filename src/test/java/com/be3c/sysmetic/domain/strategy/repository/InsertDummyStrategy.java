package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
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

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "/application-test.properties")
public class InsertDummyStrategy {

    @Autowired
    StrategyListRepository strategyListRepository;

    @Autowired
    MethodRepository methodRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    StrategyRepository strategyRepository;

    @BeforeEach
    public void init() {
        saveMethod("Manual");
        insertTraders();
    }

    // @Test
    // @DisplayName("트레이더 더미 데이터 추가")
    // @Transactional
    // @Rollback(false)
    public void insertTraders() {
        // 난수는 [1, 200]
        // int randomNum = (int) (Math.random() * 200) + 1;
        int randomNum = 300;
        System.out.println("randomNum = " + randomNum);

        for (int i=0; i < randomNum; i++) {
            Member trader = Member.builder()
                    .roleCode("trader")
                    .email("trader" + i + "@gmail.com")
                    .password("1234")
                    .name("김이박")
                    .nickname("트레이더" + i)
                    .phoneNumber("01012341234")
                    .usingStatusCode("US001")
                    .totalFollow(0)
                    .receiveInfoConsent("Yes")
                    .infoConsentDate(LocalDateTime.now())
                    .receiveMarketingConsent("NO")
                    .marketingConsentDate(LocalDateTime.now())
                    .createdBy((long) i)
                    .createdDate(LocalDateTime.now())
                    .modifiedBy((long) i)
                    .modifiedDate(LocalDateTime.now())
                    .build();
            // trader 저장
            memberRepository.save(trader);
        }
    }


    @Test
    @DisplayName("전략 더미 데이터 추가")
    @Transactional
    @Rollback(false)
    public void InsertDummyStrategies() {
        // 전략 수 난수는 [1, 199]
        // int randomStrategyNum = (int) (Math.random() * 199) + 1;
        int randomStrategyNum = 200;
        System.out.println("randomStrategyNum = " + randomStrategyNum);

        // 난수만큼 순차적으로 전략 생성
        for (int i = 0; i < randomStrategyNum; i++) {
            Strategy s = Strategy.builder()
                    .trader(getTrader("트레이더" + i))
                    .method(getMethod("Manual"))
                    .statusCode("ST001")
                    .name("전략" + (i+1))
                    .cycle('P')
                    .minOperationAmount(100.0)
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) (Math.random() * 100))
                    .accumProfitRate(Math.random() * 100)
                    .createdBy((long) randomStrategyNum)
                    .modifiedBy((long) randomStrategyNum)
                    .build();
            strategyRepository.saveAndFlush(s);        // 저장할 때는 하나씩 등록하니까 StrategyRepository 사용해서 하나씩 등록
        }
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

    Member getTrader(String nickname) {
        System.out.println("Searching for trader with nickname: " + nickname);

        return memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("해당 트레이더가 없습니다."));
    }


    Method getMethod(String name){
        return methodRepository.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("매매방식이 없습니다."));
    }
}