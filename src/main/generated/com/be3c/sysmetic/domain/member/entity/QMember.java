package com.be3c.sysmetic.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 257920356L;

    public static final QMember member = new QMember("member1");

    public final NumberPath<Long> createdBy = createNumber("createdBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> infoConsentDate = createDateTime("infoConsentDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> marketingConsentDate = createDateTime("marketingConsentDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> modifiedBy = createNumber("modifiedBy", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath receiveInfoConsent = createString("receiveInfoConsent");

    public final StringPath receiveMarketingConsent = createString("receiveMarketingConsent");

    public final StringPath roleCode = createString("roleCode");

    public final NumberPath<Integer> totalFollow = createNumber("totalFollow", Integer.class);

    public final StringPath usingStatusCode = createString("usingStatusCode");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

