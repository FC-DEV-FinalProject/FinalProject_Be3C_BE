package com.be3c.sysmetic.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInquiry is a Querydsl query type for Inquiry
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInquiry extends EntityPathBase<Inquiry> {

    private static final long serialVersionUID = 412475453L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInquiry inquiry = new QInquiry("inquiry");

    public final com.be3c.sysmetic.global.entity.QBaseEntity _super = new com.be3c.sysmetic.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QInquiryAnswer inquiryAnswer;

    public final StringPath inquiryContent = createString("inquiryContent");

    public final DateTimePath<java.time.LocalDateTime> inquiryRegistrationDate = createDateTime("inquiryRegistrationDate", java.time.LocalDateTime.class);

    public final EnumPath<InquiryStatus> inquiryStatus = createEnum("inquiryStatus", InquiryStatus.class);

    public final StringPath inquiryTitle = createString("inquiryTitle");

    public final QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final com.be3c.sysmetic.domain.strategy.entity.QStrategy strategy;

    public QInquiry(String variable) {
        this(Inquiry.class, forVariable(variable), INITS);
    }

    public QInquiry(Path<? extends Inquiry> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInquiry(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInquiry(PathMetadata metadata, PathInits inits) {
        this(Inquiry.class, metadata, inits);
    }

    public QInquiry(Class<? extends Inquiry> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.inquiryAnswer = inits.isInitialized("inquiryAnswer") ? new QInquiryAnswer(forProperty("inquiryAnswer"), inits.get("inquiryAnswer")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.strategy = inits.isInitialized("strategy") ? new com.be3c.sysmetic.domain.strategy.entity.QStrategy(forProperty("strategy"), inits.get("strategy")) : null;
    }

}

