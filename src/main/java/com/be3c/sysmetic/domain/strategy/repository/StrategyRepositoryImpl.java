package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.dto.StrategySearchRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.QStrategy;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.entity.StrategyStockReference;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class StrategyRepositoryImpl implements StrategyRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final StrategyStockReferenceRepository strategyStockReferenceRepository;
    private final StockRepository stockRepository;


    // searchByConditions : 전략 상세 조건으로 검색
    @Override
    public Page<Strategy> searchByConditions(Pageable pageable, StrategySearchRequestDto strategySearchRequestDto) {
        QStrategy strategy = QStrategy.strategy;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.and(getMethodCond(strategy, strategySearchRequestDto));
        booleanBuilder.and(getCycleCond(strategy, strategySearchRequestDto));
        booleanBuilder.and(getStockCond(strategySearchRequestDto));
        // TODO booleanBuilder.and(getPeriodCond(strategy, strategySearchRequestDto));
        booleanBuilder.and(getAccumProfitLossRateRange(strategy, strategySearchRequestDto));
        booleanBuilder.and(strategy.statusCode.eq("PUBLIC"));

        // 필터링된 결과 쿼리 실행
        List<Strategy> results = jpaQueryFactory.selectFrom(strategy)
                .where(booleanBuilder)
                .orderBy(strategy.accumProfitLossRate.desc())
                .offset(pageable.getOffset())  // 페이지 오프셋 적용
                .limit(pageable.getPageSize()) // 페이지 크기 적용
                .fetch();

        long total = jpaQueryFactory.selectFrom(strategy)
                .where(booleanBuilder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    // TODO 알고리즘별 전략 검색
    @Override
    public Page<Strategy> searchByAlgorithm(Pageable pageable, String type) {
        return null;
    }


    // 매매방식 조건
    private BooleanBuilder getMethodCond(QStrategy strategy, StrategySearchRequestDto strategySearchRequestDto) {
        List<String> methodCond = strategySearchRequestDto.getMethods();

        if (methodCond == null || methodCond.isEmpty())
            return new BooleanBuilder();

        return new BooleanBuilder(strategy.method.name.in(methodCond));
    }

    // 주기 조건
    private BooleanBuilder getCycleCond(QStrategy strategy, StrategySearchRequestDto strategySearchRequestDto) {
        List<Character> cycleCond = strategySearchRequestDto.getCycle();

        if (cycleCond == null || cycleCond.isEmpty())
            return new BooleanBuilder();

        return new BooleanBuilder(strategy.cycle.in(cycleCond));
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
            booleanBuilder.or(QStrategy.strategy.id.eq(ref.getStrategy().getId()));

        return booleanBuilder;
    }

    // TODO 전략 통계 서비스 calculateOperationPeriod - 전략 통계 매핑해서 가져오기
    // private BooleanBuilder getPeriodCond(QStrategy strategy, StrategySearchRequestDto strategySearchRequestDto) {
    //     BooleanBuilder booleanBuilder = new BooleanBuilder();
    //     List<String> periodCond = strategySearchRequestDto.getPeriods();
    //
    //     if (periodCond == null || periodCond.isEmpty())
    //         return booleanBuilder;
    //
    //     for (String period : periodCond){
    //         switch (period) {
    //             case "Total" :
    //                 break;
    //
    //             case "1 Year Or Less" :
    //                 booleanBuilder.and(strategy.period.loe)
    //                 break;
    //
    //         }
    //     }
    //
    //     return null;
    // }

    // 누적수익률 조건
    private BooleanBuilder getAccumProfitLossRateRange(QStrategy strategy, StrategySearchRequestDto strategySearchRequestDto) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (!checkAccumProfitLossRateRange(strategySearchRequestDto))
            return booleanBuilder;

        Double start = Double.parseDouble(strategySearchRequestDto.getAccumProfitLossRateRangeStart());
        Double end = Double.parseDouble(strategySearchRequestDto.getAccumProfitLossRateRangeEnd());

        booleanBuilder.and(strategy.accumProfitLossRate.between(start, end));

        return booleanBuilder;
    }

    // 누적수익률 조건 범위 null & 크기 올바른지 확인
    private Boolean checkAccumProfitLossRateRange(StrategySearchRequestDto strategySearchRequestDto) {
        String accumProfitLossRateRangeStartCond = strategySearchRequestDto.getAccumProfitLossRateRangeStart();
        String accumProfitLossRateRangeEndCond = strategySearchRequestDto.getAccumProfitLossRateRangeEnd();

        // 조건이 없거나 비어있으면 false 반환
        if (accumProfitLossRateRangeStartCond == null || accumProfitLossRateRangeEndCond == null ||
                accumProfitLossRateRangeStartCond.isEmpty() || accumProfitLossRateRangeEndCond.isEmpty())
            return false;

        // 시작 범위가 끝 범위보다 큰 경우 false 반환
        if (Double.parseDouble(accumProfitLossRateRangeStartCond) > Double.parseDouble(accumProfitLossRateRangeEndCond)) {
            return false;
        }

        return true;
    }

}
