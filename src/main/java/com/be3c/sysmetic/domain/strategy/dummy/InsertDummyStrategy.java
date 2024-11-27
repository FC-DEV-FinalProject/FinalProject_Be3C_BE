package com.be3c.sysmetic.domain.strategy.dummy;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.domain.strategy.repository.StockRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.util.DoubleHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class InsertDummyStrategy implements CommandLineRunner {

    private final MethodRepository methodRepository;
    private final MemberRepository memberRepository;
    private final StrategyRepository strategyRepository;
    private final StockRepository stockRepository;
    // private final StrategyStockReferenceRepository strategyStockReferenceRepository;
    // private final InsertStrategyServiceImpl insertStrategyServiceImpl;
    private final DoubleHandler doubleHandler;


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 더미 데이터 삽입 로직
        insertDummyData();
    }

    public void insertDummyData() {
        // Method 더미 데이터
        Method method = Method.builder()
                .name("Manual")
                .statusCode("MS001")
                .methodCreatedDate(LocalDateTime.now())
                .build();
        // method 저장
        methodRepository.saveAndFlush(method);

        // Stock 더미 데이터
        Stock domesticStocks = Stock.builder()
                .name("domesticStocks")
                .statusCode("USING")
                .build();
        stockRepository.saveAndFlush(domesticStocks);

        Stock overseaStocks = Stock.builder()
                .name("overseaStocks")
                .statusCode("USING")
                .build();
        stockRepository.saveAndFlush(overseaStocks);

        // Member trader 더미 데이터
        for (int i = 0; i < 300; i++) {
            Member trader = Member.builder()
                    .roleCode("trader")
                    .email("trader" + i + "@gmail.com")
                    .password("1234")
                    .name("홍길동" + i)
                    .nickname("트레이더" + i)
                    .birth(LocalDate.now())
                    .phoneNumber("0101234" + String.format("%04d", i))
                    .usingStatusCode("US001")
                    .totalFollow(0)
                    .totalStrategyCount(0)
                    .receiveInfoConsent("Yes")
                    .infoConsentDate(LocalDateTime.now())
                    .receiveMarketingConsent("NO")
                    .marketingConsentDate(LocalDateTime.now())
                    .build();
            // trader 저장
            memberRepository.saveAndFlush(trader);
        }

        // 전략 등록
        for (int i = 0; i < 20; i++) {
            Strategy s = Strategy.builder()
                    .trader(getTrader("트레이더" + i))
                    .method(getMethod())
                    .statusCode("PUBLIC")
                    .name("전략" + (i + 1))
                    .cycle('P')
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) ((Math.random() * 100) + 1))
                    .accumulatedProfitLossRate(doubleHandler.cutDouble(Math.random() * 100))
                    .mdd(doubleHandler.cutDouble(Math.random() * 100))
                    .kpRatio(doubleHandler.cutDouble(Math.random() * 100))
                    .build();
            strategyRepository.saveAndFlush(s);
        }

        // 트레이더 별 전략 목록
        for (int i = 0; i < 20; i++) {
            Strategy s = Strategy.builder()
                    .trader(getTrader("트레이더9"))
                    .method(getMethod())
                    .statusCode("PUBLIC")
                    .name("트레이더9의 전략" + (i + 1))
                    .cycle('P')
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) ((Math.random() * 100) + 1))
                    .accumulatedProfitLossRate(doubleHandler.cutDouble(Math.random() * 100))
                    .mdd(doubleHandler.cutDouble(Math.random() * 100))
                    .kpRatio(doubleHandler.cutDouble(Math.random() * 100))
                    .build();
            strategyRepository.saveAndFlush(s);
        }

        // 비공개 전략 추가
        for (int i = 0; i < 20; i++) {
            Strategy s = Strategy.builder()
                    .trader(getTrader("트레이더8"))
                    .method(getMethod())
                    .statusCode("PRIVATE")
                    .name("트레이더8의 전략" + (i + 1))
                    .cycle('P')
                    .content("전략" + (i + 1) + " 소개 내용")
                    .followerCount((long) ((Math.random() * 100) + 1))
                    .accumulatedProfitLossRate(doubleHandler.cutDouble(Math.random() * 100))
                    .mdd(doubleHandler.cutDouble(Math.random() * 100))
                    .kpRatio(doubleHandler.cutDouble(Math.random() * 100))
                    .build();
            strategyRepository.saveAndFlush(s);
        }

        // // 등록된 전략 가져오기
        // List<Strategy> uploadedStrategies = strategyRepository.findAllUsingState();
        //
        // // 전략에 종목 매핑
        // for (Strategy s : uploadedStrategies) {
        //     insertStrategyServiceImpl.insertStrategyStockReference(s, List.of(domesticStocks.getId(), overseaStocks.getId()), s.getTrader().getId());
        // }
    }

    private Member getTrader(String nickname) {
        return memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("해당 트레이더가 없습니다."));
    }

    private Method getMethod() {
        return methodRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("매매방식이 없습니다."));
    }
}