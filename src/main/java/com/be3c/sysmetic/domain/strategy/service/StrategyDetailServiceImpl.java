package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailDto;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStatistics;
import com.be3c.sysmetic.domain.strategy.repository.StrategyDetailRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyStatisticsRepository;
import com.be3c.sysmetic.domain.strategy.util.DoubleHandler;
import com.be3c.sysmetic.domain.strategy.util.StockGetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategyDetailServiceImpl implements StrategyDetailService {

    private final StrategyDetailRepository strategyDetailRepository;
    private final StrategyStatisticsRepository strategyStatisticsRepository;
    private final StockGetter stockGetter;
    private final DoubleHandler doubleHandler;

    @Override
    @Transactional
    public StrategyDetailDto getDetail(Long id) {

        String statusCode = "PUBLIC";

        StrategyStatistics statistics = strategyStatisticsRepository.findByStrategyId(id);

        return strategyDetailRepository.findByIdAndStatusCode(id, statusCode)
                .map(strategy -> StrategyDetailDto.builder()
                        .id(strategy.getId())
                        .traderId(strategy.getTrader().getId())
                        .traderNickname(strategy.getTrader().getNickname())
                        .methodId(strategy.getMethod().getId())
                        .methodName(strategy.getMethod().getName())
                        .stockList(stockGetter.getStocks(strategy.getId()))
                        .name(strategy.getName())
                        .statusCode(strategy.getStatusCode())
                        .cycle(strategy.getCycle())
                        .content(strategy.getContent())
                        .followerCount(strategy.getFollowerCount())
                        .mdd(doubleHandler.cutDouble(strategy.getMdd()))
                        .kpRatio(doubleHandler.cutDouble(strategy.getKpRatio()))
                        .smScore(doubleHandler.cutDouble(strategy.getSmScore()))
                        .accumulatedProfitLossRate(doubleHandler.cutDouble(strategy.getAccumulatedProfitLossRate()))
                        .maximumCapitalReductionAmount(doubleHandler.cutDouble(statistics.getMaximumCapitalReductionAmount()))
                        .averageProfitLossRate(doubleHandler.cutDouble(statistics.getAverageProfitLossRate()))
                        .profitFactor(doubleHandler.cutDouble(statistics.getProfitFactor()))
                        .winningRate(doubleHandler.cutDouble(statistics.getWinningRate()))
                        .build())
                .orElseThrow(() -> new NoSuchElementException("해당 전략의 상세 보기 페이지가 존재하지 않습니다."));
    }
}