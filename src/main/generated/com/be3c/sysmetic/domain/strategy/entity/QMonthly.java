package com.be3c.sysmetic.domain.strategy.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMonthly is a Querydsl query type for Monthly
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMonthly extends EntityPathBase<Monthly> {

    private static final long serialVersionUID = -1250483894L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMonthly monthly = new QMonthly("monthly");

    public final com.be3c.sysmetic.global.entity.QBaseEntity _super = new com.be3c.sysmetic.global.entity.QBaseEntity(this);

    public final NumberPath<Double> accumulatedProfitLossAmount = createNumber("accumulatedProfitLossAmount", Double.class);

    public final NumberPath<Double> accumulatedProfitLossRate = createNumber("accumulatedProfitLossRate", Double.class);

    public final NumberPath<Double> averageMonthlyPrincipal = createNumber("averageMonthlyPrincipal", Double.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final NumberPath<Integer> monthNumber = createNumber("monthNumber", Integer.class);

    public final NumberPath<Double> profitLossAmount = createNumber("profitLossAmount", Double.class);

    public final NumberPath<Double> profitLossRate = createNumber("profitLossRate", Double.class);

    public final QStrategy strategy;

    public final NumberPath<Integer> yearNumber = createNumber("yearNumber", Integer.class);

    public QMonthly(String variable) {
        this(Monthly.class, forVariable(variable), INITS);
    }

    public QMonthly(Path<? extends Monthly> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMonthly(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMonthly(PathMetadata metadata, PathInits inits) {
        this(Monthly.class, metadata, inits);
    }

    public QMonthly(Class<? extends Monthly> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.strategy = inits.isInitialized("strategy") ? new QStrategy(forProperty("strategy"), inits.get("strategy")) : null;
    }

}

