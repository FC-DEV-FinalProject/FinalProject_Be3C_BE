package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.domain.strategy.entity.Daily;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.entity.StrategyGraphAnalysis;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStatistics;
import com.be3c.sysmetic.domain.strategy.exception.StrategyBadRequestException;
import com.be3c.sysmetic.domain.strategy.exception.StrategyExceptionMessage;
import com.be3c.sysmetic.domain.strategy.repository.*;
import com.be3c.sysmetic.domain.strategy.util.DoubleHandler;
import com.be3c.sysmetic.domain.strategy.util.StockGetter;
import com.be3c.sysmetic.domain.strategy.util.StrategyIndicatorsCalculator;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategyDetailServiceImpl implements StrategyDetailService {

    private final StrategyRepository strategyRepository;
    private final StrategyDetailRepository strategyDetailRepository;
    private final StrategyStatisticsRepository strategyStatisticsRepository;
    private final StrategyGraphAnalysisRepository strategyGraphAnalysisRepository;
    private final MonthlyRepository monthlyRepository;
    private final DailyRepository dailyRepository;
    private final StockGetter stockGetter;
    private final DoubleHandler doubleHandler;
    private final FileService fileService;
    private final StrategyIndicatorsCalculator strategyIndicatorsCalculator;

    @Override
    @Transactional
    public StrategyDetailDto getDetail(Long id) {

        StrategyDetailStatistics statistics = strategyStatisticsRepository.findStrategyDetailStatistics(id);

        return strategyDetailRepository.findPublicStrategy(id)
                .map(strategy -> StrategyDetailDto.builder()
                        .id(strategy.getId())
                        .traderId(strategy.getTrader().getId())
                        .traderNickname(strategy.getTrader().getNickname())
                        .traderProfileImage(fileService.getFilePathNullable(new FileRequest(FileReferenceType.MEMBER, strategy.getTrader().getId())))
                        .methodId(strategy.getMethod().getId())
                        .methodName(strategy.getMethod().getName())
                        .methodIconPath(fileService.getFilePathNullable(new FileRequest(FileReferenceType.METHOD, strategy.getMethod().getId())))
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
                        .maximumCapitalReductionAmount(statistics.getMaximumCapitalReductionAmount())
                        .averageProfitLossRate(doubleHandler.cutDouble(statistics.getAverageProfitLossRate()))
                        .profitFactor(doubleHandler.cutDouble(statistics.getProfitFactor()))
                        .winningRate(strategy.getWinningRate())
                        .monthlyRecord(getMonthlyRecords(strategy.getId()))
                        .build())
                .orElseThrow(() -> new NoSuchElementException("전략 상세 페이지가 존재하지 않습니다."));
    }


    // 분석 지표 그래프 데이터 요청
    @Override
    @Transactional
    public StrategyAnalysisResponseDto getAnalysis(Long strategyId, StrategyAnalysisOption optionOne, StrategyAnalysisOption optionTwo, String period) {

        StrategyAnalysisResponseDto analysis = strategyRepository.findGraphAnalysis(strategyId, optionOne, optionTwo, period);

        if (analysis == null || analysis.getXAxis().isEmpty() || analysis.getYAxis().isEmpty()) return null;

        return analysis;
    }


    // saveAnalysis - 분석 지표 그래프 데이터 생성 (일간 분석 등록하면, 그래프 데이터도 생성)
    @Override
    @Transactional
    public APIResponse<String> saveAnalysis(Long strategyId, LocalDate date) {
        Daily daily = dailyRepository.findByStrategyIdAndDate(strategyId, date);
        StrategyStatistics statistics = strategyStatisticsRepository.findByStrategyId(strategyId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));
        Double accumulatedProfitLossAmount = daily.getAccumulatedProfitLossAmount();
        Double standardAmount = daily.getStandardAmount();
        Double maximumCapitalReductionAmount = statistics.getMaximumCapitalReductionAmount();

        StrategyGraphAnalysis data = StrategyGraphAnalysis.builder()
                .strategy(strategyRepository.findByIdAndOpenStatusCode(strategyId).orElseThrow())
                .daily(daily)
                .date(daily.getDate())
                .standardAmount(daily.getStandardAmount())
                .currentBalance(daily.getCurrentBalance())
                .principal(daily.getPrincipal())
                .accumulatedDepositWithdrawalAmount(dailyRepository.findTotalDepositWithdrawalAmountByStrategyId(strategyId))
                .depositWithdrawalAmount(daily.getDepositWithdrawalAmount())
                .profitLossAmount(daily.getProfitLossAmount())
                .profitLossRate(daily.getProfitLossRate())
                .accumulatedProfitLossAmount(daily.getAccumulatedProfitLossAmount())
                .currentCapitalReductionAmount(strategyIndicatorsCalculator.calCurrentCapitalReductionAmount(strategyId, accumulatedProfitLossAmount))
                .currentCapitalReductionRate(strategyIndicatorsCalculator.calCurrentCapitalReductionRate(strategyId, standardAmount))
                .averageProfitLossAmount(strategyIndicatorsCalculator.calAverageProfitLossAmount(strategyId, accumulatedProfitLossAmount))
                .averageProfitLossRate(strategyIndicatorsCalculator.calAverageProfitLossRate(strategyId, accumulatedProfitLossAmount))
                .winningRate(strategyIndicatorsCalculator.calWinningRate(strategyId))
                .profitFactor(strategyIndicatorsCalculator.calProfitFactor(strategyId))
                .roa(strategyIndicatorsCalculator.calRoa(accumulatedProfitLossAmount, maximumCapitalReductionAmount))
                .maximumCapitalReductionAmount(maximumCapitalReductionAmount)
                .build();

        strategyGraphAnalysisRepository.save(data);
        return APIResponse.success("분석 지표 그래프 데이터 생성 성공");
    }


    // updateAnalysis - 분석 지표 그래프 데이터 수정(일간 분석 데이터 수정하면, 그래프 데이터도 수정)
    @Override
    @Transactional
    public APIResponse<String> updateAnalysis(Long strategyId, Long dailyId, LocalDate date) {
        Daily newDaily = dailyRepository.findByStrategyIdAndDate(strategyId, date);
        StrategyStatistics statistics = strategyStatisticsRepository.findByStrategyId(strategyId).orElseThrow(() ->
                new StrategyBadRequestException(StrategyExceptionMessage.DATA_NOT_FOUND.getMessage(), ErrorCode.NOT_FOUND));
        Double accumulatedProfitLossAmount = statistics.getAccumulatedProfitLossAmount();
        Double standardAmount = newDaily.getStandardAmount();
        Double maximumCapitalReductionAmount = statistics.getMaximumCapitalReductionAmount();

        StrategyGraphAnalysis data = StrategyGraphAnalysis.builder()
                .strategy(strategyRepository.findByIdAndOpenStatusCode(strategyId).orElseThrow())
                .daily(newDaily)
                .date(newDaily.getDate())
                .standardAmount(newDaily.getStandardAmount())
                .currentBalance(newDaily.getCurrentBalance())
                .principal(newDaily.getPrincipal())
                .accumulatedDepositWithdrawalAmount(dailyRepository.findTotalDepositWithdrawalAmountByStrategyId(strategyId))
                .depositWithdrawalAmount(newDaily.getDepositWithdrawalAmount())
                .profitLossAmount(newDaily.getProfitLossAmount())
                .profitLossRate(newDaily.getProfitLossRate())
                .accumulatedProfitLossAmount(newDaily.getAccumulatedProfitLossAmount())
                .currentCapitalReductionAmount(strategyIndicatorsCalculator.calCurrentCapitalReductionAmount(strategyId, accumulatedProfitLossAmount))
                .currentCapitalReductionRate(strategyIndicatorsCalculator.calCurrentCapitalReductionRate(strategyId, standardAmount))
                .averageProfitLossAmount(strategyIndicatorsCalculator.calAverageProfitLossAmount(strategyId, accumulatedProfitLossAmount))
                .averageProfitLossRate(strategyIndicatorsCalculator.calAverageProfitLossRate(strategyId, accumulatedProfitLossAmount))
                .winningRate(strategyIndicatorsCalculator.calWinningRate(strategyId))
                .profitFactor(strategyIndicatorsCalculator.calProfitFactor(strategyId))
                .roa(strategyIndicatorsCalculator.calRoa(accumulatedProfitLossAmount, maximumCapitalReductionAmount))
                .maximumCapitalReductionAmount(maximumCapitalReductionAmount)
                .build();

        strategyGraphAnalysisRepository.save(data);
        return APIResponse.success("분석 지표 그래프 데이터 생성 성공");
    }


    // deleteAnalysis - 일간 분석 삭제하면, 해당 날짜 그래프 데이터 삭제
    @Override
    @Transactional
    public APIResponse<String> deleteAnalysis(Long strategyId, Long dailyId, Daily deletedDaily) {

        Strategy strategy = strategyRepository.findById(strategyId).orElseThrow(() -> new IllegalArgumentException("Strategy not fount with ID : " + strategyId));

        // DB에서 삭제
        strategyGraphAnalysisRepository.deleteByDailyId(dailyId);

        // 빼야 하는 값
        Double profitLossAmountToMinus = deletedDaily.getProfitLossAmount();
        Double profitLossRateToMinus = deletedDaily.getProfitLossRate();
        Double depositWithdrawalAmountToMinus = deletedDaily.getDepositWithdrawalAmount();

        List<StrategyGraphAnalysis> toBeModified = strategyGraphAnalysisRepository.findByIdAndAfterDate(strategyId, deletedDaily.getDate());

        toBeModified.forEach(analysis -> {
            analysis.setAccumulatedDepositWithdrawalAmount(analysis.getAccumulatedDepositWithdrawalAmount() - depositWithdrawalAmountToMinus);
            analysis.setAverageProfitLossAmount(analysis.getAverageProfitLossAmount() - profitLossAmountToMinus);
            analysis.setAverageProfitLossRate(analysis.getAverageProfitLossRate() - profitLossRateToMinus);
            analysis.setWinningRate(calculateUpdatedWinningRate(strategyId, analysis.getDate()));
            analysis.setProfitFactor(calculateUpdatedProfitFactor(strategyId, analysis.getDate()));
            analysis.setRoa(calculateUpdatedRoa(strategyId, analysis.getDate(), analysis.getAccumulatedProfitLossAmount()));
            analysis.setMaximumCapitalReductionAmount(strategyGraphAnalysisRepository.findTop1MaximumCapitalReductionAmountByStrategyId(strategyId));
        });

        strategyGraphAnalysisRepository.saveAll(toBeModified);

        return APIResponse.success("그래프 데이터 삭제 성공");
    }

    // 그 시점의 winningRate 계산
    private Double calculateUpdatedWinningRate(Long strategyId, LocalDate date) {
        Long tradingDays = strategyGraphAnalysisRepository.countAllByStrategyIdBeforeDate(strategyId, date);
        if (tradingDays == 0) return 0.0;

        Long profitDays = strategyGraphAnalysisRepository.countProfitDaysByStrategyIdBeforeDate(strategyId, date);

        return doubleHandler.cutDouble(tradingDays / (double) profitDays);
    }

    // 그 시점의 Profit Factor 계산
    private Double calculateUpdatedProfitFactor(Long strategyId, LocalDate date) {
        Object[] profitLossAmounts = strategyGraphAnalysisRepository.findProfitLossAmount(strategyId, date);
        if (profitLossAmounts == null) return 0.0;
        Double profit = (Double) profitLossAmounts[0];
        Double loss = (Double) profitLossAmounts[1];
        if (loss == 0) return 0.0;
        return doubleHandler.cutDouble(profit / loss);
    }

    // 그 시점의 ROA 계산
    private Double calculateUpdatedRoa(Long strategyId, LocalDate date, Double accumulatedProfitLossAmount) {
        Double maximumCapitalReductionAmount = strategyGraphAnalysisRepository.findMaximumCapitalReductionAmountBeforeDate(strategyId, date);

        if (maximumCapitalReductionAmount == 0) return 0.0;

        return doubleHandler.cutDouble(accumulatedProfitLossAmount / maximumCapitalReductionAmount * -1);
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