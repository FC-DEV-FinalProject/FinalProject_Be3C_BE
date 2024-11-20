package com.be3c.sysmetic.domain.strategy.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMethod is a Querydsl query type for Method
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMethod extends EntityPathBase<Method> {

    private static final long serialVersionUID = 1058972676L;

    public static final QMethod method = new QMethod("method");

    public final com.be3c.sysmetic.global.entity.QBaseEntity _super = new com.be3c.sysmetic.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> methodCreatedDate = createDateTime("methodCreatedDate", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final StringPath name = createString("name");

    public final StringPath statusCode = createString("statusCode");

    public QMethod(String variable) {
        super(Method.class, forVariable(variable));
    }

    public QMethod(Path<? extends Method> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMethod(PathMetadata metadata) {
        super(Method.class, metadata);
    }

}

