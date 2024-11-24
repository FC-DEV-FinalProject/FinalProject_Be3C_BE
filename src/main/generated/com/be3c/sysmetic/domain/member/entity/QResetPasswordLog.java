package com.be3c.sysmetic.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QResetPasswordLog is a Querydsl query type for ResetPasswordLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QResetPasswordLog extends EntityPathBase<ResetPasswordLog> {

    private static final long serialVersionUID = 530884644L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QResetPasswordLog resetPasswordLog = new QResetPasswordLog("resetPasswordLog");

    public final com.be3c.sysmetic.global.entity.QBaseEntity _super = new com.be3c.sysmetic.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final DateTimePath<java.time.LocalDateTime> tryDate = createDateTime("tryDate", java.time.LocalDateTime.class);

    public final StringPath tryIp = createString("tryIp");

    public QResetPasswordLog(String variable) {
        this(ResetPasswordLog.class, forVariable(variable), INITS);
    }

    public QResetPasswordLog(Path<? extends ResetPasswordLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QResetPasswordLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QResetPasswordLog(PathMetadata metadata, PathInits inits) {
        this(ResetPasswordLog.class, metadata, inits);
    }

    public QResetPasswordLog(Class<? extends ResetPasswordLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

