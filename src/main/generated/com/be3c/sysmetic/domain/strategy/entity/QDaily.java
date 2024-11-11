package com.be3c.sysmetic.domain.strategy.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDaily is a Querydsl query type for Daily
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDaily extends EntityPathBase<Daily> {

    private static final long serialVersionUID = -1775396202L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDaily daily = new QDaily("daily");

    public final NumberPath<Double> accumulatedProfitLossAmount = createNumber("accumulatedProfitLossAmount", Double.class);

    public final NumberPath<Double> accumulatedProfitLossRate = createNumber("accumulatedProfitLossRate", Double.class);

    public final NumberPath<Long> createdBy = createNumber("createdBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dailyCreatedDate = createDateTime("dailyCreatedDate", java.time.LocalDateTime.class);

    public final NumberPath<Double> dailyLossAmount = createNumber("dailyLossAmount", Double.class);

    public final NumberPath<Double> dailyLossRate = createNumber("dailyLossRate", Double.class);

    public final DateTimePath<java.time.LocalDateTime> date = createDateTime("date", java.time.LocalDateTime.class);

    public final NumberPath<Double> depositWithdrawalAmount = createNumber("depositWithdrawalAmount", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> modifiedBy = createNumber("modifiedBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final NumberPath<Double> principal = createNumber("principal", Double.class);

    public final QStrategy strategy;

    public QDaily(String variable) {
        this(Daily.class, forVariable(variable), INITS);
    }

    public QDaily(Path<? extends Daily> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDaily(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDaily(PathMetadata metadata, PathInits inits) {
        this(Daily.class, metadata, inits);
    }

    public QDaily(Class<? extends Daily> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.strategy = inits.isInitialized("strategy") ? new QStrategy(forProperty("strategy"), inits.get("strategy")) : null;
    }

}

