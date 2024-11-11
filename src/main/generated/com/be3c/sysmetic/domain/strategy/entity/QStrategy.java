package com.be3c.sysmetic.domain.strategy.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStrategy is a Querydsl query type for Strategy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStrategy extends EntityPathBase<Strategy> {

    private static final long serialVersionUID = 1996503510L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStrategy strategy = new QStrategy("strategy");

    public final StringPath content = createString("content");

    public final NumberPath<Long> createdBy = createNumber("createdBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final ComparablePath<Character> cycle = createComparable("cycle", Character.class);

    public final NumberPath<Long> followerCount = createNumber("followerCount", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> kpRatio = createNumber("kpRatio", Double.class);

    public final QMethod method;

    public final NumberPath<Double> minOperationAmount = createNumber("minOperationAmount", Double.class);

    public final NumberPath<Long> modifiedBy = createNumber("modifiedBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final NumberPath<Double> smScore = createNumber("smScore", Double.class);

    public final StringPath statusCode = createString("statusCode");

    public final DateTimePath<java.time.LocalDateTime> strategyCreatedDate = createDateTime("strategyCreatedDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> strategyModifiedDate = createDateTime("strategyModifiedDate", java.time.LocalDateTime.class);

    public final com.be3c.sysmetic.domain.member.entity.QMember trader;

    public QStrategy(String variable) {
        this(Strategy.class, forVariable(variable), INITS);
    }

    public QStrategy(Path<? extends Strategy> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStrategy(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStrategy(PathMetadata metadata, PathInits inits) {
        this(Strategy.class, metadata, inits);
    }

    public QStrategy(Class<? extends Strategy> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.method = inits.isInitialized("method") ? new QMethod(forProperty("method")) : null;
        this.trader = inits.isInitialized("trader") ? new com.be3c.sysmetic.domain.member.entity.QMember(forProperty("trader")) : null;
    }

}

