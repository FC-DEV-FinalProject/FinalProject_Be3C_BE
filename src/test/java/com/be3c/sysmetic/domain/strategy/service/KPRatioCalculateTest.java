package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.StrategyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Daily;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.*;
import com.be3c.sysmetic.domain.strategy.util.StrategyCalculator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@SpringBootTest
public class KPRatioCalculateTest {

    private final MemberRepository memberRepository;
    private final StockRepository stockRepository;
    private final MethodRepository methodRepository;
    private final StrategyStockReferenceRepository strategyStockReferenceRepository;
    private final StrategyRepository strategyRepository;
    private final TraderStrategyServiceImpl traderStrategyService;
    private final DailyRepository dailyRepository;
    private final MonthlyRepository monthlyRepository;
    private final DailyServiceImpl dailyServiceImpl;
    private final StrategyStatisticsRepository statisticsRepository;
    private final StrategyStatisticsServiceImpl statisticsService;
    private final StrategyServiceImpl strategyService;
    private final StrategyCalculator strategyCalculator;

    @BeforeEach
    void setUp() {
        // save
        memberRepository.save(getMember());
        stockRepository.save(getStock());
        methodRepository.save(getMethod());

        // delete
        statisticsRepository.deleteAll();
        dailyRepository.deleteAll();
        monthlyRepository.deleteAll();
        strategyStockReferenceRepository.deleteAll();
        strategyRepository.deleteAll();
    }

    @Test
    void test() {
        List<Daily> dailyList = getDailyList();

        Double highProfitLossRate = 0.0;
        Double minDrawDown = 0.0;
        Double sumDrawDown = 0.0;
        Long sumDrawDownPeriod = 0L;

        for(int i=0; i<dailyList.size(); i++) {
            Double currentProfitLossRate = dailyList.get(i).getAccumulatedProfitLossRate();

            if(highProfitLossRate > currentProfitLossRate) {
                // 손익률 인하되는 시점
                sumDrawDownPeriod++;
                if(currentProfitLossRate - highProfitLossRate < minDrawDown) {
                    // DD 갱신
                    minDrawDown = currentProfitLossRate - highProfitLossRate;
                }
            } else {
                highProfitLossRate = currentProfitLossRate;
                sumDrawDown += minDrawDown;
                minDrawDown = 0.0;
            }
        }

        Double accumulatedProfitLossRate = dailyList.stream().mapToDouble(Daily::getProfitLossRate).sum();

        Double kpRatio = strategyCalculator.getKpRatio(accumulatedProfitLossRate, sumDrawDown, sumDrawDownPeriod, Long.valueOf(dailyList.stream().toList().size()));

        assertEquals(Math.round(kpRatio * 100.0) / 100.0, 1.97);
    }

    // get member
    private Member getMember() {
        return Member.builder()
                .roleCode("TRADER")
                .email("sysmetic_tester@sysmetic.com")
                .password("sys1234!")
                .name("한감자")
                .nickname("감자")
                .birth(LocalDateTime.now().toLocalDate())
                .phoneNumber("01022223333")
                .usingStatusCode("")
                .totalFollow(160)
                .totalStrategyCount(4)
                .receiveInfoConsent("Y")
                .infoConsentDate(LocalDateTime.now())
                .receiveMarketingConsent("Y")
                .marketingConsentDate(LocalDateTime.now())
                .build();
    }

    // get stock
    private Stock getStock() {
        return Stock.builder()
                .name("국내ETF")
                .statusCode("PUBLIC")
                .build();
    }

    // get method
    private Method getMethod() {
        return Method.builder()
                .name("Auto")
                .statusCode("PUBLIC")
                .build();
    }

    private StrategyPostRequestDto getStrategy() {
        return StrategyPostRequestDto.builder()
                .methodId(findMethod().getId())
                .stockIdList(List.of(findStrategy().getId()))
                .name("test")
                .content("content")
                .cycle('P')
                .build();
    }

    // find member
    private Member findMember() {
        return memberRepository.findAll().stream().findFirst().orElse(null);
    }

    // find method
    private Method findMethod() {
        return methodRepository.findAll().stream().findFirst().orElse(null);
    }

    // find Stock
    private Stock findStock() {
        return stockRepository.findAll().stream().findFirst().orElse(null);
    }

    private Strategy findStrategy() { return strategyRepository.findAll().stream().findFirst().orElse(null); }

    // get insert strategy request dto
    private StrategyPostRequestDto getStrategyPostRequestDto() {
        return StrategyPostRequestDto.builder()
                .methodId(findMethod().getId())
                .stockIdList(List.of(findStock().getId()))
                .name("테스트 전략")
                .content("테스트 전략입니다.")
                .cycle('P')
                .build();
    }

    private List<Daily> getDailyList() {
        List<Daily> dailyList = new ArrayList<>();

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 3))
                .profitLossRate(0.0)
                .accumulatedProfitLossRate(0.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 5))
                .profitLossRate(9.0)
                .accumulatedProfitLossRate(9.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 6))
                .profitLossRate(5.0)
                .accumulatedProfitLossRate(14.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 7))
                .profitLossRate(2.0)
                .accumulatedProfitLossRate(16.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 8))
                .profitLossRate(1.0)
                .accumulatedProfitLossRate(17.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 9))
                .profitLossRate(-5.0)
                .accumulatedProfitLossRate(12.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 12))
                .profitLossRate(-3.0)
                .accumulatedProfitLossRate(9.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 13))
                .profitLossRate(6.0)
                .accumulatedProfitLossRate(15.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 14))
                .profitLossRate(3.0)
                .accumulatedProfitLossRate(18.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 15))
                .profitLossRate(5.0)
                .accumulatedProfitLossRate(23.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 16))
                .profitLossRate(6.0)
                .accumulatedProfitLossRate(29.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 19))
                .profitLossRate(-10.0)
                .accumulatedProfitLossRate(19.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 20))
                .profitLossRate(0.0)
                .accumulatedProfitLossRate(19.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 21))
                .profitLossRate(-8.0)
                .accumulatedProfitLossRate(11.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 22))
                .profitLossRate(1.0)
                .accumulatedProfitLossRate(12.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 23))
                .profitLossRate(7.0)
                .accumulatedProfitLossRate(19.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 26))
                .profitLossRate(2.0)
                .accumulatedProfitLossRate(21.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 27))
                .profitLossRate(-6.0)
                .accumulatedProfitLossRate(15.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 28))
                .profitLossRate(-7.0)
                .accumulatedProfitLossRate(8.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 29))
                .profitLossRate(10.0)
                .accumulatedProfitLossRate(18.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 1, 30))
                .profitLossRate(8.0)
                .accumulatedProfitLossRate(26.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 2, 2))
                .profitLossRate(7.0)
                .accumulatedProfitLossRate(33.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 2, 3))
                .profitLossRate(7.0)
                .accumulatedProfitLossRate(40.0)
                .build());

        dailyList.add(Daily.builder()
                .date(LocalDate.of(2015, 2, 4))
                .profitLossRate(2.0)
                .accumulatedProfitLossRate(42.0)
                .build());

        return dailyList;
    }

}
