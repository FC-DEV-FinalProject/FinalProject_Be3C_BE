package com.be3c.sysmetic.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInterestStrategy is a Querydsl query type for InterestStrategy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInterestStrategy extends EntityPathBase<InterestStrategy> {

    private static final long serialVersionUID = -1908116729L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInterestStrategy interestStrategy = new QInterestStrategy("interestStrategy");

    public final com.be3c.sysmetic.global.entity.QBaseEntity _super = new com.be3c.sysmetic.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final QFolder folder;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final StringPath statusCode = createString("statusCode");

    public final com.be3c.sysmetic.domain.strategy.entity.QStrategy strategy;

    public QInterestStrategy(String variable) {
        this(InterestStrategy.class, forVariable(variable), INITS);
    }

    public QInterestStrategy(Path<? extends InterestStrategy> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInterestStrategy(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInterestStrategy(PathMetadata metadata, PathInits inits) {
        this(InterestStrategy.class, metadata, inits);
    }

    public QInterestStrategy(Class<? extends InterestStrategy> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.folder = inits.isInitialized("folder") ? new QFolder(forProperty("folder"), inits.get("folder")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.strategy = inits.isInitialized("strategy") ? new com.be3c.sysmetic.domain.strategy.entity.QStrategy(forProperty("strategy"), inits.get("strategy")) : null;
    }

}

