package com.be3c.sysmetic.domain.strategy.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStrategyApprovalHistory is a Querydsl query type for StrategyApprovalHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStrategyApprovalHistory extends EntityPathBase<StrategyApprovalHistory> {

    private static final long serialVersionUID = -538186725L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStrategyApprovalHistory strategyApprovalHistory = new QStrategyApprovalHistory("strategyApprovalHistory");

    public final com.be3c.sysmetic.global.entity.QBaseEntity _super = new com.be3c.sysmetic.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.be3c.sysmetic.domain.member.entity.QMember manager;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final StringPath statusCode = createString("statusCode");

    public final QStrategy strategy;

    public final DateTimePath<java.time.LocalDateTime> strategyApprovalDate = createDateTime("strategyApprovalDate", java.time.LocalDateTime.class);

    public QStrategyApprovalHistory(String variable) {
        this(StrategyApprovalHistory.class, forVariable(variable), INITS);
    }

    public QStrategyApprovalHistory(Path<? extends StrategyApprovalHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStrategyApprovalHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStrategyApprovalHistory(PathMetadata metadata, PathInits inits) {
        this(StrategyApprovalHistory.class, metadata, inits);
    }

    public QStrategyApprovalHistory(Class<? extends StrategyApprovalHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.manager = inits.isInitialized("manager") ? new com.be3c.sysmetic.domain.member.entity.QMember(forProperty("manager")) : null;
        this.strategy = inits.isInitialized("strategy") ? new QStrategy(forProperty("strategy"), inits.get("strategy")) : null;
    }

}

