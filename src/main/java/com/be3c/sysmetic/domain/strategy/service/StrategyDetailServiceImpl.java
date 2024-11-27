package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.MonthlyRecord;
import com.be3c.sysmetic.domain.strategy.dto.StrategyAnalysisOption;
import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailStatistics;
import com.be3c.sysmetic.domain.strategy.repository.MonthlyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyDetailRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyStatisticsRepository;
import com.be3c.sysmetic.domain.strategy.util.DoubleHandler;
import com.be3c.sysmetic.domain.strategy.util.StockGetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategyDetailServiceImpl implements StrategyDetailService {

    private final StrategyDetailRepository strategyDetailRepository;
    private final StrategyStatisticsRepository strategyStatisticsRepository;
    private final MonthlyRepository monthlyRepository;
    private final StockGetter stockGetter;
    private final DoubleHandler doubleHandler;

    @Override
    @Transactional
    public StrategyDetailDto getDetail(Long id, StrategyAnalysisOption optionOne, StrategyAnalysisOption optionTwo) {

        String statusCode = "PUBLIC";

        StrategyDetailStatistics statistics = strategyStatisticsRepository.findStrategyDetailStatistics(id);

        // if (optionOne == optionTwo) strategyRepository.
        // else strategyRepository.

        return strategyDetailRepository.findByIdAndStatusCode(id, statusCode)
                .map(strategy -> StrategyDetailDto.builder()
                        .id(strategy.getId())
                        .traderNickname(strategy.getTrader().getNickname())
                        .methodId(strategy.getMethod().getId())
                        .methodName(strategy.getMethod().getName())
                        .stockList(stockGetter.getStocks(strategy.getId()))
                        .name(strategy.getName())
                        .statusCode(strategy.getStatusCode())
                        .cycle(strategy.getCycle())
                        .content(strategy.getContent())
                        .followerCount(strategy.getFollowerCount())
                        .mdd(strategy.getMdd())
                        .kpRatio(strategy.getKpRatio())
                        .smScore(strategy.getSmScore())
                        .accumulatedProfitLossRate(strategy.getAccumulatedProfitLossRate())
                        .maximumCapitalReductionAmount(doubleHandler.cutDouble(statistics.getMaximumCapitalReductionAmount()))
                        .averageProfitLossRate(doubleHandler.cutDouble(statistics.getAverageProfitLossRate()))
                        .profitFactor(doubleHandler.cutDouble(statistics.getProfitFactor()))
                        .winningRate(doubleHandler.cutDouble(statistics.getWinningRate()))
                        .monthlyRecord(getMonthlyRecords(strategy.getId()))
                        // TODO analysis 추가
                        .build())
                .orElseThrow(() -> new NoSuchElementException("상세 페이지 존재하지 않음"));
    }

    // getMonthlyRecords : 월간 분석 데이터 가져오기
    private List<MonthlyRecord> getMonthlyRecords(Long strategyId) {

        List<MonthlyRecord> monthlyRecords = monthlyRepository.findAllMonthlyRecord(strategyId);

        // 월간 데이터가 없으면 빈 ArrayList 반환
        if (monthlyRecords == null || monthlyRecords.isEmpty())
            return new ArrayList<>();

        List<MonthlyRecord> result = new ArrayList<>(monthlyRecords);
        Double ytd = 0.0;
        Integer currentYear = monthlyRecords.get(0).getYear();

        for (MonthlyRecord m : monthlyRecords) {
            // 다른 년도면 YTD 추가
            if (!m.getYear().equals(currentYear)) {
                result.add(MonthlyRecord.builder()
                        .year(currentYear)
                        .month(13)
                        .accumulatedProfitLossRate(doubleHandler.cutDouble(ytd))
                        .build());

                currentYear = m.getYear();
                ytd = 0.0;
            }
            ytd += m.getAccumulatedProfitLossRate();
        }

        // 마지막 연도의 YTD 추가
        result.add(MonthlyRecord.builder()
                .year(currentYear)
                .month(13)
                .accumulatedProfitLossRate(doubleHandler.cutDouble(ytd))
                .build());

        return result;
    }
}