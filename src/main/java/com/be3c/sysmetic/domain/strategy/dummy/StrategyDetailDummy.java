package com.be3c.sysmetic.domain.strategy.dummy;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Component
public class StrategyDetailDummy implements CommandLineRunner {

    // @Autowired
    // StockRepository stockRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MethodRepository methodRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private StrategyDetailRepository strategyDetailRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        methodRepository.deleteAll();
        memberRepository.deleteAll();
        strategyRepository.deleteAll();
        insertMethodDummyData();
        insertTraderDummyData();
        // 종목 더미 데이터
        insertStrategyDummyData();
    }

    // Method 매매 방식 더미 데이터
    @Transactional
    public void insertMethodDummyData() {
        // Method 더미 데이터
        Method method = Method.builder()
                .name("Manual")
                .statusCode("MS001")
                .methodCreatedDate(LocalDateTime.now())
                .build();
        // method 저장
        methodRepository.save(method);
    }

    // Member trader 더미 데이터
    @Transactional
    public void insertTraderDummyData() {
        // Member trader 더미 데이터
        for (int i = 0; i <= 300; i++) {
            Member trader = Member.builder()
                    .roleCode("trader")
                    .email("trader" + i + "@gmail.com")
                    .password("1234")
                    .name("홍길동" + i)
                    .nickname("트레이더" + i)
                    .phoneNumber("0101234" + String.format("%04d", i))
                    .usingStatusCode("US001")
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
    }

    // Strategy 전략 더미 데이터
    @Transactional
    public void insertStrategyDummyData() {
        for (int i = 0; i < 200; i++) {
            Strategy s = Strategy.builder()
                    .trader(getTrader("트레이더300"))
                    .method(getMethod())
                    .statusCode("ST001")
                    .name("전략" + (i + 1))
                    .cycle('P')
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) ((Math.random() * 100) + 1))
                    .accumProfitLossRate(Math.random() * 100)
                    .build();
            strategyRepository.saveAndFlush(s);
        }
    }


    private Member getTrader(String nickname) {
        return memberRepository.findDistinctByNickname(nickname).stream().findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 트레이더가 없습니다."));
    }

    private Method getMethod() {
        return methodRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("매매방식이 없습니다."));
    }

}