package com.be3c.sysmetic.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEmailAuthLog is a Querydsl query type for EmailAuthLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmailAuthLog extends EntityPathBase<EmailAuthLog> {

    private static final long serialVersionUID = -838331254L;

    public static final QEmailAuthLog emailAuthLog = new QEmailAuthLog("emailAuthLog");

    public final StringPath authCode = createString("authCode");

    public final StringPath authStatusCode = createString("authStatusCode");

    public final NumberPath<Long> createdBy = createNumber("createdBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> expiredDate = createDateTime("expiredDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> modifiedBy = createNumber("modifiedBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final StringPath receiveEmail = createString("receiveEmail");

    public final StringPath tryIp = createString("tryIp");

    public QEmailAuthLog(String variable) {
        super(EmailAuthLog.class, forVariable(variable));
    }

    public QEmailAuthLog(Path<? extends EmailAuthLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEmailAuthLog(PathMetadata metadata) {
        super(EmailAuthLog.class, metadata);
    }

}

