package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.domain.strategy.repository.MonthlyRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyDetailRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyStatisticsRepository;
import com.be3c.sysmetic.domain.strategy.util.DoubleHandler;
import com.be3c.sysmetic.domain.strategy.util.StockGetter;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.service.FileService;
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
    private final FileService fileService;

    @Override
    @Transactional
    public StrategyDetailDto getDetail(Long id) {

        // TODO 아래 메서드 사용
        // StrategyDetailStatistics statistics = strategyStatisticsRepository.findStrategyDetailStatistics(id);
        // TODO Optional 적용; 추후에 DB에 초기값 추가하면 삭제
        StrategyDetailStatistics statistics = strategyStatisticsRepository.findStrategyDetailStatistics(id)
                .orElseGet(this::createDefaultStatistics);

        return strategyDetailRepository.findPublicStrategy(id)
                .map(strategy -> {
                    List<String> stockIconPaths = new ArrayList<>();

                    stockGetter.getStocks(strategy.getId()).getStockIds().forEach(stockId ->
                            stockIconPaths.add(fileService.getFilePath(new FileRequest(FileReferenceType.STOCK, stockId)))
                    );

                    return StrategyDetailDto.builder()
                                    .id(strategy.getId())
                                    .traderId(strategy.getTrader().getId())
                                    .traderProfileImage(fileService.getFilePath(new FileRequest(FileReferenceType.MEMBER, strategy.getTrader().getId())))
                                    .traderNickname(strategy.getTrader().getNickname())
                                    .methodIconPath(fileService.getFilePath(new FileRequest(FileReferenceType.METHOD, strategy.getMethod().getId())))
                                    .stockIconPath(stockIconPaths)
                                    .name(strategy.getName())
                                    .statusCode(strategy.getStatusCode())
                                    .cycle(strategy.getCycle())
                                    .content(strategy.getContent())
                                    .followerCount(strategy.getFollowerCount())
                                    .mdd(strategy.getMdd())
                                    .kpRatio(strategy.getKpRatio())
                                    .smScore(strategy.getSmScore())
                                    .accumulatedProfitLossRate(strategy.getAccumulatedProfitLossRate())
                                    .maximumCapitalReductionAmount(statistics.getMaximumCapitalReductionAmount())
                                    .averageProfitLossRate(doubleHandler.cutDouble(statistics.getAverageProfitLossRate()))
                                    .profitFactor(doubleHandler.cutDouble(statistics.getProfitFactor()))
                                    .winningRate(strategy.getWinningRate())
                                    .monthlyRecord(getMonthlyRecords(strategy.getId()))
                                    .build();
            })
                    .orElseThrow(() -> new NoSuchElementException("전략 상세 페이지가 존재하지 않습니다."));
    }


    // TODO 추후 DB에 초기값 추가하면 삭제
    private StrategyDetailStatistics createDefaultStatistics() {
        return new StrategyDetailStatistics(
                0.0,
                0.0,
                0.0,
                0.0
        );
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