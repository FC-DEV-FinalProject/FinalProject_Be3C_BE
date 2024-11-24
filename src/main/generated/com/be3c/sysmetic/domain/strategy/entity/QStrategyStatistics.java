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

    public final com.be3c.sysmetic.global.entity.QBaseEntity _super = new com.be3c.sysmetic.global.entity.QBaseEntity(this);

    public final NumberPath<Double> accumulatedDepositWithdrawalAmount = createNumber("accumulatedDepositWithdrawalAmount", Double.class);

    public final NumberPath<Double> accumulatedProfitLossAmount = createNumber("accumulatedProfitLossAmount", Double.class);

    public final NumberPath<Double> accumulatedProfitLossRate = createNumber("accumulatedProfitLossRate", Double.class);

    public final NumberPath<Double> averageProfitLossAmount = createNumber("averageProfitLossAmount", Double.class);

    public final NumberPath<Double> averageProfitLossRate = createNumber("averageProfitLossRate", Double.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Double> currentBalance = createNumber("currentBalance", Double.class);

    public final NumberPath<Double> currentCapitalReductionAmount = createNumber("currentCapitalReductionAmount", Double.class);

    public final NumberPath<Double> currentCapitalReductionRate = createNumber("currentCapitalReductionRate", Double.class);

    public final NumberPath<Long> currentContinuousProfitLossDays = createNumber("currentContinuousProfitLossDays", Long.class);

    public final DatePath<java.time.LocalDate> firstRegistrationDate = createDate("firstRegistrationDate", java.time.LocalDate.class);

    public final NumberPath<Long> highPointRenewalProgress = createNumber("highPointRenewalProgress", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.time.LocalDate> lastRegistrationDate = createDate("lastRegistrationDate", java.time.LocalDate.class);

    public final NumberPath<Double> maximumAccumulatedProfitLossAmount = createNumber("maximumAccumulatedProfitLossAmount", Double.class);

    public final NumberPath<Double> maximumAccumulatedProfitLossRate = createNumber("maximumAccumulatedProfitLossRate", Double.class);

    public final NumberPath<Double> maximumCapitalReductionAmount = createNumber("maximumCapitalReductionAmount", Double.class);

    public final NumberPath<Double> maximumCapitalReductionRate = createNumber("maximumCapitalReductionRate", Double.class);

    public final NumberPath<Long> maximumContinuousLossDays = createNumber("maximumContinuousLossDays", Long.class);

    public final NumberPath<Long> maximumContinuousProfitDays = createNumber("maximumContinuousProfitDays", Long.class);

    public final NumberPath<Double> maximumDailyLossAmount = createNumber("maximumDailyLossAmount", Double.class);

    public final NumberPath<Double> maximumDailyLossRate = createNumber("maximumDailyLossRate", Double.class);

    public final NumberPath<Double> maximumDailyProfitAmount = createNumber("maximumDailyProfitAmount", Double.class);

    public final NumberPath<Double> maximumDailyProfitRate = createNumber("maximumDailyProfitRate", Double.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final NumberPath<Double> principal = createNumber("principal", Double.class);

    public final NumberPath<Double> profitFactor = createNumber("profitFactor", Double.class);

    public final NumberPath<Double> roa = createNumber("roa", Double.class);

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

