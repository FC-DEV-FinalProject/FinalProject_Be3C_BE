package com.be3c.sysmetic.domain.strategy.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStrategyStatistics is a Querydsl query type for StrategyStatistics
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStrategyStatistics extends EntityPathBase<StrategyStatistics> {

    private static final long serialVersionUID = -1960250471L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStrategyStatistics strategyStatistics = new QStrategyStatistics("strategyStatistics");

    public final NumberPath<Double> accumulatedDepositWithdrawalAmount = createNumber("accumulatedDepositWithdrawalAmount", Double.class);

    public final NumberPath<Double> accumulatedProfitAmount = createNumber("accumulatedProfitAmount", Double.class);

    public final NumberPath<Double> accumulatedProfitLossAmount = createNumber("accumulatedProfitLossAmount", Double.class);

    public final NumberPath<Double> accumulatedProfitLossRate = createNumber("accumulatedProfitLossRate", Double.class);

    public final NumberPath<Double> accumulatedProfitRate = createNumber("accumulatedProfitRate", Double.class);

    public final NumberPath<Double> averageProfitLossAmount = createNumber("averageProfitLossAmount", Double.class);

    public final NumberPath<Double> averageProfitLossRate = createNumber("averageProfitLossRate", Double.class);

    public final NumberPath<Long> createdBy = createNumber("createdBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final NumberPath<Double> currentBalance = createNumber("currentBalance", Double.class);

    public final NumberPath<Double> currentCapitalReductionAmount = createNumber("currentCapitalReductionAmount", Double.class);

    public final NumberPath<Double> currentCapitalReductionRate = createNumber("currentCapitalReductionRate", Double.class);

    public final NumberPath<Long> currentContinuousProfitLossDays = createNumber("currentContinuousProfitLossDays", Long.class);

    public final DateTimePath<java.time.LocalDateTime> firstRegistrationDate = createDateTime("firstRegistrationDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> highPointRenewalProgress = createDateTime("highPointRenewalProgress", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> lastRegistrationDate = createDateTime("lastRegistrationDate", java.time.LocalDateTime.class);

    public final NumberPath<Double> lastYearProfitRate = createNumber("lastYearProfitRate", Double.class);

    public final NumberPath<Long> maxContinuousLossDays = createNumber("maxContinuousLossDays", Long.class);

    public final NumberPath<Long> maxContinuousProfitDays = createNumber("maxContinuousProfitDays", Long.class);

    public final NumberPath<Double> maximumCapitalReductionAmount = createNumber("maximumCapitalReductionAmount", Double.class);

    public final NumberPath<Double> maximumCapitalReductionRate = createNumber("maximumCapitalReductionRate", Double.class);

    public final NumberPath<Double> maximumDailyLossAmount = createNumber("maximumDailyLossAmount", Double.class);

    public final NumberPath<Double> maximumDailyLossRate = createNumber("maximumDailyLossRate", Double.class);

    public final NumberPath<Double> maximumDailyProfitAmount = createNumber("maximumDailyProfitAmount", Double.class);

    public final NumberPath<Double> maximumDailyProfitRate = createNumber("maximumDailyProfitRate", Double.class);

    public final NumberPath<Long> modifiedBy = createNumber("modifiedBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final NumberPath<Double> principal = createNumber("principal", Double.class);

    public final NumberPath<Double> profitFactor = createNumber("profitFactor", Double.class);

    public final NumberPath<Double> roa = createNumber("roa", Double.class);

    public final NumberPath<Double> standardDeviation = createNumber("standardDeviation", Double.class);

    public final QStrategy strategy;

    public final NumberPath<Long> totalLossDays = createNumber("totalLossDays", Long.class);

    public final NumberPath<Long> totalProfitDays = createNumber("totalProfitDays", Long.class);

    public final NumberPath<Long> totalTradingDays = createNumber("totalTradingDays", Long.class);

    public final NumberPath<Double> winningRate = createNumber("winningRate", Double.class);

    public QStrategyStatistics(String variable) {
        this(StrategyStatistics.class, forVariable(variable), INITS);
    }

    public QStrategyStatistics(Path<? extends StrategyStatistics> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStrategyStatistics(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStrategyStatistics(PathMetadata metadata, PathInits inits) {
        this(StrategyStatistics.class, metadata, inits);
    }

    public QStrategyStatistics(Class<? extends StrategyStatistics> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.strategy = inits.isInitialized("strategy") ? new QStrategy(forProperty("strategy"), inits.get("strategy")) : null;
    }

}

