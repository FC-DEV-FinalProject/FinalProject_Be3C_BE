package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.dto.StrategyAnalysisResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategySearchRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyStatusCode;
import com.be3c.sysmetic.domain.strategy.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.be3c.sysmetic.domain.strategy.entity.QStrategy.strategy;
import static com.be3c.sysmetic.domain.strategy.entity.QStrategyGraphAnalysis.strategyGraphAnalysis;

@RequiredArgsConstructor
public class StrategyRepositoryImpl implements StrategyRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final StrategyStockReferenceRepository strategyStockReferenceRepository;
    private final StockRepository stockRepository;


    // searchByConditions : 전략 상세 조건으로 검색
    @Override
    public Page<Strategy> searchByConditions(Pageable pageable, StrategySearchRequestDto strategySearchRequestDto) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.and(getMethodCond(strategySearchRequestDto));
        booleanBuilder.and(getCycleCond(strategySearchRequestDto));
        booleanBuilder.and(getStockCond(strategySearchRequestDto));
        booleanBuilder.and(getPeriodCond(strategySearchRequestDto));
        booleanBuilder.and(getAccumulatedProfitLossRateRange(strategySearchRequestDto));
        booleanBuilder.and(strategy.statusCode.eq(String.valueOf(StrategyStatusCode.PUBLIC)));

        // 필터링된 결과 쿼리 실행
        List<Strategy> results = jpaQueryFactory.selectFrom(strategy)
                .where(booleanBuilder)
                .orderBy(strategy.accumulatedProfitLossRate.desc())
                .offset(pageable.getOffset())  // 페이지 오프셋 적용
                .limit(pageable.getPageSize()) // 페이지 크기 적용
                .fetch();

        long total = jpaQueryFactory.selectFrom(strategy)
                .where(booleanBuilder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }


    // searchByAlgorithm : 알고리즘별 전략 정렬
    @Override
    public Page<Strategy> searchByAlgorithm(Pageable pageable, String algorithm) {

        // PUBLIC 상태인 전략만 검색
        BooleanBuilder booleanBuilder = new BooleanBuilder(strategy.statusCode.eq(String.valueOf(StrategyStatusCode.PUBLIC)));

        OrderSpecifier<?>[] orderSpecifiers = null;

        switch (algorithm) {
            case "EFFICIENCY":
                orderSpecifiers = new OrderSpecifier[] {
                        strategy.accumulatedProfitLossRate
                                .divide(strategy.mdd)
                                .desc()
                };
                break;

            case "OFFENSIVE":
                orderSpecifiers = new OrderSpecifier[] {
                        strategy.accumulatedProfitLossRate
                                .divide(
                                        strategy.winningRate.multiply(0.01).subtract(1)
                                )
                                .desc()
                };
                break;

            // TODO 방어형 DEFENSIVE
            // case "DEFENSIVE":
            //     // 누적 수익률 데이터 가져오기
            //     NumberExpression<Double> averageProfitLossRate = strategyStatistics.averageProfitLossRate.doubleValue();
            //
            //
        }

        List<Strategy> result = jpaQueryFactory
                .selectFrom(strategy)
                .where(booleanBuilder)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(result, pageable, result.size());
    }


    /*
        findGraphAnalysis : 분석 그래프 데이터 - optionOne, optionTwo, period 에 해당하는 컬럼 반환
    */
    @Override
    public StrategyAnalysisResponseDto findGraphAnalysis(Long strategyId) {

        List<StrategyGraphAnalysis> result = jpaQueryFactory
                .selectFrom(strategyGraphAnalysis)
                .where(strategyGraphAnalysis.strategy.id.eq(strategyId))
                .orderBy(strategyGraphAnalysis.date.asc())
                .fetch();

        if (result.isEmpty()) return null;

        List<String> dates = result.stream()
                .map(data -> data.getDate().toString())
                .collect(Collectors.toList());


        return StrategyAnalysisResponseDto.builder()
                .xAxis(dates)
                .standardAmounts(result.stream()
                                .map(analysis -> analysis.getStandardAmount())
                                .collect(Collectors.toList()))
                .currentBalance(result.stream()
                        .map(analysis -> analysis.getCurrentBalance())
                        .collect(Collectors.toList()))
                .principal(result.stream()
                        .map(analysis -> analysis.getPrincipal())
                        .collect(Collectors.toList()))
                .accumulatedDepositWithdrawalAmount(result.stream()
                        .map(analysis -> analysis.getAccumulatedDepositWithdrawalAmount())
                        .collect(Collectors.toList()))
                .depositWithdrawalAmount(result.stream()
                        .map(analysis -> analysis.getDepositWithdrawalAmount())
                        .collect(Collectors.toList()))
                .dailyProfitLossAmount(result.stream()
                        .map(analysis -> analysis.getProfitLossAmount())
                        .collect(Collectors.toList()))
                .dailyProfitLossRate(result.stream()
                        .map(analysis -> analysis.getProfitLossRate())
                        .collect(Collectors.toList()))
                .accumulatedProfitLossAmount(result.stream()
                        .map(analysis -> analysis.getAccumulatedProfitLossAmount())
                        .collect(Collectors.toList()))
                .currentCapitalReductionAmount(result.stream()
                        .map(analysis -> analysis.getCurrentCapitalReductionAmount())
                        .collect(Collectors.toList()))
                .currentCapitalReductionRate(result.stream()
                        .map(analysis -> analysis.getCurrentCapitalReductionRate())
                        .collect(Collectors.toList()))
                .averageProfitLossAmount(result.stream()
                        .map(analysis -> analysis.getAverageProfitLossAmount())
                        .collect(Collectors.toList()))
                .averageProfitLossRate(result.stream()
                        .map(analysis -> analysis.getAverageProfitLossRate())
                        .collect(Collectors.toList()))
                .winningRate(result.stream()
                        .map(analysis -> analysis.getWinningRate())
                        .collect(Collectors.toList()))
                .profitFactor(result.stream()
                        .map(analysis -> analysis.getProfitFactor())
                        .collect(Collectors.toList()))
                .roa(result.stream()
                        .map(analysis -> analysis.getRoa())
                        .collect(Collectors.toList()))
                .build();
    }


    // 매매방식 조건
    private BooleanBuilder getMethodCond(StrategySearchRequestDto strategySearchRequestDto) {
        List<String> methodCond = strategySearchRequestDto.getMethods();

        if (methodCond == null || methodCond.isEmpty())
            return new BooleanBuilder();

        return new BooleanBuilder(QStrategy.strategy.method.name.in(methodCond));
    }

    // 주기 조건
    private BooleanBuilder getCycleCond(StrategySearchRequestDto strategySearchRequestDto) {
        List<String> cycleCond = strategySearchRequestDto.getCycle();

        if (cycleCond == null || cycleCond.isEmpty())
            return new BooleanBuilder();

        List<Character> cycleChars = cycleCond.stream()
                .map(s -> s.charAt(0))
                .toList();

        return new BooleanBuilder(QStrategy.strategy.cycle.in(cycleChars));
    }

    // 종목 조건
    private BooleanBuilder getStockCond(StrategySearchRequestDto strategySearchRequestDto) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        List<String> stockCond = strategySearchRequestDto.getStockNames();

        if (stockCond == null || stockCond.isEmpty())
            return booleanBuilder;      // 위에 새로 생성해서 아무것도 없는 booleanBuilder 반환

        // 종목 이름 리스트로 Stock ID 가져오기
        List<Long> stockIds = stockRepository.findIdsByNames(stockCond);

        if (stockIds == null || stockIds.isEmpty())
            return booleanBuilder;

        // Stock ID로 StrategyStockReference 가져오기
        List<StrategyStockReference> references = strategyStockReferenceRepository.findAllByStockIds(stockIds);

        // 각 참조를 통해 Strategy 조건 추가
        for (StrategyStockReference ref : references)
            booleanBuilder.or(strategy.id.eq(ref.getStrategy().getId()));

        return booleanBuilder;
    }

    // 운용 기간 조건
    private BooleanBuilder getPeriodCond(StrategySearchRequestDto strategySearchRequestDto) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (strategySearchRequestDto.getPeriod() == null || strategySearchRequestDto.getPeriod().isEmpty())
            return booleanBuilder;

        switch (strategySearchRequestDto.getPeriod())  {
            case "ALL" :
                return booleanBuilder;
            case "LESS_THAN_YEAR" :
                // 1년 이하 : StrategyGraphAnalysis의 개수가 365 이하인 경우
                return booleanBuilder.and(
                        JPAExpressions.select(strategyGraphAnalysis.count())
                                .from(strategyGraphAnalysis)
                                .where(strategyGraphAnalysis.strategy.id.eq(strategy.id))
                                .loe(365L));

            case "ONE_TO_TWO_YEAR" :
                // 1~2년 : 개수가 366~730인 경우
                return booleanBuilder.and(
                        JPAExpressions.select(strategyGraphAnalysis.count())
                                .from(strategyGraphAnalysis)
                                .where(strategyGraphAnalysis.strategy.id.eq(strategy.id))
                                .goe(366L)
                                .and(
                                        JPAExpressions.select(strategyGraphAnalysis.count())
                                                .from(strategyGraphAnalysis)
                                                .where(strategyGraphAnalysis.strategy.id.eq(strategy.id))
                                                .loe(730L)
                                ));

            case "TWO_TO_THREE_YEAR":
                // 2~3년 : 개수가 731 이상, 1095 이하인 경우
                return booleanBuilder.and(
                        JPAExpressions.select(strategyGraphAnalysis.count())
                                .from(strategyGraphAnalysis)
                                .where(strategyGraphAnalysis.strategy.id.eq(strategy.id))
                                .goe(731L)
                                .and(
                                        JPAExpressions.select(strategyGraphAnalysis.count())
                                                .from(strategyGraphAnalysis)
                                                .where(strategyGraphAnalysis.strategy.id.eq(strategy.id))
                                                .loe(1095L)
                                ));

            case "THREE_YEAR_MORE":
                // 3년 이상 : 개수가 1095인 경우
                return booleanBuilder.and(
                        JPAExpressions.select(strategyGraphAnalysis.count())
                                .from(strategyGraphAnalysis)
                                .where(strategyGraphAnalysis.strategy.id.eq(strategy.id))
                                .gt(1005L));
            default :
                return booleanBuilder;
        }
    }


    // 누적수익률 조건
    private BooleanBuilder getAccumulatedProfitLossRateRange(StrategySearchRequestDto strategySearchRequestDto) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (!checkAccumulatedProfitLossRateRange(strategySearchRequestDto))
            return booleanBuilder;

        Double start = Double.parseDouble(strategySearchRequestDto.getAccumulatedProfitLossRateRangeStart());
        Double end = Double.parseDouble(strategySearchRequestDto.getAccumulatedProfitLossRateRangeEnd());

        booleanBuilder.and(QStrategy.strategy.accumulatedProfitLossRate.between(start, end));

        return booleanBuilder;
    }

    // 누적수익률 조건 범위 null & 크기 올바른지 확인
    private Boolean checkAccumulatedProfitLossRateRange(StrategySearchRequestDto strategySearchRequestDto) {
        String accumulatedProfitLossRateRangeStartCond = strategySearchRequestDto.getAccumulatedProfitLossRateRangeStart();
        String accumulatedProfitLossRateRangeEndCond = strategySearchRequestDto.getAccumulatedProfitLossRateRangeEnd();

        // 조건이 없거나 비어있으면 false 반환
        if (accumulatedProfitLossRateRangeStartCond == null || accumulatedProfitLossRateRangeEndCond == null ||
                accumulatedProfitLossRateRangeStartCond.isEmpty() || accumulatedProfitLossRateRangeEndCond.isEmpty())
            return false;

        // 시작 범위가 끝 범위보다 큰 경우 false 반환
        if (Double.parseDouble(accumulatedProfitLossRateRangeStartCond) > Double.parseDouble(accumulatedProfitLossRateRangeEndCond)) {
            return false;
        }
        return true;
    }
}
