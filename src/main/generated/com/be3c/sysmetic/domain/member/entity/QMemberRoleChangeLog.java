package com.be3c.sysmetic.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberRoleChangeLog is a Querydsl query type for MemberRoleChangeLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberRoleChangeLog extends EntityPathBase<MemberRoleChangeLog> {

    private static final long serialVersionUID = 1095991962L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberRoleChangeLog memberRoleChangeLog = new QMemberRoleChangeLog("memberRoleChangeLog");

    public final QMember admin;

    public final NumberPath<Long> createdBy = createNumber("createdBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final NumberPath<Long> modifiedBy = createNumber("modifiedBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final StringPath roleChangeCode = createString("roleChangeCode");

    public QMemberRoleChangeLog(String variable) {
        this(MemberRoleChangeLog.class, forVariable(variable), INITS);
    }

    public QMemberRoleChangeLog(Path<? extends MemberRoleChangeLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberRoleChangeLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberRoleChangeLog(PathMetadata metadata, PathInits inits) {
        this(MemberRoleChangeLog.class, metadata, inits);
    }

    public QMemberRoleChangeLog(Class<? extends MemberRoleChangeLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.admin = inits.isInitialized("admin") ? new QMember(forProperty("admin")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

