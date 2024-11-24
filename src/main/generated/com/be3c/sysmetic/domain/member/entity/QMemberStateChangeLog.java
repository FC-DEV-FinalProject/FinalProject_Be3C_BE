package com.be3c.sysmetic.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberStateChangeLog is a Querydsl query type for MemberStateChangeLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberStateChangeLog extends EntityPathBase<MemberStateChangeLog> {

    private static final long serialVersionUID = 263930471L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberStateChangeLog memberStateChangeLog = new QMemberStateChangeLog("memberStateChangeLog");

    public final NumberPath<Long> createdBy = createNumber("createdBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember manager;

    public final QMember member;

    public final NumberPath<Long> modifiedBy = createNumber("modifiedBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final StringPath stateChangeCode = createString("stateChangeCode");

    public QMemberStateChangeLog(String variable) {
        this(MemberStateChangeLog.class, forVariable(variable), INITS);
    }

    public QMemberStateChangeLog(Path<? extends MemberStateChangeLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberStateChangeLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberStateChangeLog(PathMetadata metadata, PathInits inits) {
        this(MemberStateChangeLog.class, metadata, inits);
    }

    public QMemberStateChangeLog(Class<? extends MemberStateChangeLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.manager = inits.isInitialized("manager") ? new QMember(forProperty("manager")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

