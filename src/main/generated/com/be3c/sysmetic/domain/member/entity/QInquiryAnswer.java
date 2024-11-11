package com.be3c.sysmetic.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInquiryAnswer is a Querydsl query type for InquiryAnswer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInquiryAnswer extends EntityPathBase<InquiryAnswer> {

    private static final long serialVersionUID = 414788187L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInquiryAnswer inquiryAnswer = new QInquiryAnswer("inquiryAnswer");

    public final com.be3c.sysmetic.global.entity.QBaseEntity _super = new com.be3c.sysmetic.global.entity.QBaseEntity(this);

    public final StringPath answerContent = createString("answerContent");

    public final DateTimePath<java.time.LocalDateTime> answerRegistrationDate = createDateTime("answerRegistrationDate", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QInquiry inquiry;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public QInquiryAnswer(String variable) {
        this(InquiryAnswer.class, forVariable(variable), INITS);
    }

    public QInquiryAnswer(Path<? extends InquiryAnswer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInquiryAnswer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInquiryAnswer(PathMetadata metadata, PathInits inits) {
        this(InquiryAnswer.class, metadata, inits);
    }

    public QInquiryAnswer(Class<? extends InquiryAnswer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.inquiry = inits.isInitialized("inquiry") ? new QInquiry(forProperty("inquiry"), inits.get("inquiry")) : null;
    }

}

