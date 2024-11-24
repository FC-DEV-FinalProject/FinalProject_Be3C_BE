package com.be3c.sysmetic.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFindEmailLog is a Querydsl query type for FindEmailLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFindEmailLog extends EntityPathBase<FindEmailLog> {

    private static final long serialVersionUID = 638959147L;

    public static final QFindEmailLog findEmailLog = new QFindEmailLog("findEmailLog");

    public final NumberPath<Long> createdBy = createNumber("createdBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> modifiedBy = createNumber("modifiedBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final StringPath successStatusCode = createString("successStatusCode");

    public final DateTimePath<java.time.LocalDateTime> tryDate = createDateTime("tryDate", java.time.LocalDateTime.class);

    public final StringPath tryIp = createString("tryIp");

    public final StringPath tryName = createString("tryName");

    public final StringPath tryPhoneNumber = createString("tryPhoneNumber");

    public QFindEmailLog(String variable) {
        super(FindEmailLog.class, forVariable(variable));
    }

    public QFindEmailLog(Path<? extends FindEmailLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFindEmailLog(PathMetadata metadata) {
        super(FindEmailLog.class, metadata);
    }

}

