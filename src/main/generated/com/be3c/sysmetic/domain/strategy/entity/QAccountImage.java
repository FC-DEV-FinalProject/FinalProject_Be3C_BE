package com.be3c.sysmetic.domain.strategy.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAccountImage is a Querydsl query type for AccountImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccountImage extends EntityPathBase<AccountImage> {

    private static final long serialVersionUID = -612552687L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAccountImage accountImage = new QAccountImage("accountImage");

    public final com.be3c.sysmetic.global.entity.QBaseEntity _super = new com.be3c.sysmetic.global.entity.QBaseEntity(this);

    public final DateTimePath<java.time.LocalDateTime> accountImageCreatedAt = createDateTime("accountImageCreatedAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final QStrategy strategy;

    public final StringPath title = createString("title");

    public QAccountImage(String variable) {
        this(AccountImage.class, forVariable(variable), INITS);
    }

    public QAccountImage(Path<? extends AccountImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAccountImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAccountImage(PathMetadata metadata, PathInits inits) {
        this(AccountImage.class, metadata, inits);
    }

    public QAccountImage(Class<? extends AccountImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.strategy = inits.isInitialized("strategy") ? new QStrategy(forProperty("strategy"), inits.get("strategy")) : null;
    }

}

