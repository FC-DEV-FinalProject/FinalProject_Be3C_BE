package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.entity.QInquiry;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InquiryRepository {

    private final EntityManager em;
//    private final EnableSpringDataWebSupport.QuerydslActivator querydslActivator;

    public void save(Inquiry inquiry) {
        if (inquiry.getId() == null) {
            em.persist(inquiry);
        } else {
            em.merge(inquiry);
        }
    }

    public Inquiry findOne(Long id) {
        return em.find(Inquiry.class, id);
    }

    public List<Inquiry> findAll(int offset, int limit) {
        return em.createQuery("select i from Inquiry i order by i.inquiryRegistrationDate desc", Inquiry.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCountAll() {
        return em.createQuery("select count(i) from Inquiry i", Long.class)
                .getSingleResult();
    }

    public List<Inquiry> findByInquiryStatus(InquiryStatus inquiryStatus, int offset, int limit) {
        return em.createQuery("select i from Inquiry i where i.inquiryStatus = :inquiryStatus order by i.inquiryRegistrationDate desc", Inquiry.class)
                .setParameter("inquiryStatus", inquiryStatus)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCountInquiryStatus(InquiryStatus inquiryStatus) {
        return em.createQuery("select count(i) from Inquiry i where i.inquiryStatus = :inquiryStatus", Long.class)
                .setParameter("inquiryStatus", inquiryStatus)
                .getSingleResult();
    }

    public List<Inquiry> findByMemberId(Long memberId, int offset, int limit) {
        return em.createQuery("select i from Inquiry i where i.member.id = :memberId order by i.inquiryRegistrationDate desc", Inquiry.class)
                .setParameter("memberId", memberId)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCountMemberId(Long memberId) {
        return em.createQuery("select count(i) from Inquiry i where i.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    public List<Inquiry> findByMemberIdAndInquiryStatus(Long memberId, InquiryStatus inquiryStatus, int offset, int limit) {
        return em.createQuery("select i from Inquiry i where i.member.id = :memberId and i.inquiryStatus = :inquiryStatus order by i.inquiryRegistrationDate desc", Inquiry.class)
                .setParameter("memberId", memberId)
                .setParameter("inquiryStatus", inquiryStatus)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCountMemberIdAndInquiryStatus(Long memberId, InquiryStatus inquiryStatus) {
        return em.createQuery("select count(i) from Inquiry i where i.member.id = :memberId and i.inquiryStatus = :inquiryStatus", Long.class)
                .setParameter("memberId", memberId)
                .setParameter("inquiryStatus", inquiryStatus)
                .getSingleResult();
    }

    public void deleteInquiry(Inquiry inquiry) {
        em.remove(inquiry);
    }

    private final JPAQueryFactory jpaQueryFactory;

    public List<Inquiry> dynamicQueryWithBooleanBuilder(InquirySearch inquirySearch) {
//        em.createQuery("select i, m, s from Inquiry i left join i.member m left join i.strategy s");
//        em.createQuery("select i from Inquiry i join fetch i.member join fetch i.strategy " +
//                "where i.strategy.name like concat('%', :strategyKeyword, '%')", Inquiry.class)
//                .setParameter("strategyKeyword", inquirySearch.getStrategyKeyword())
//                .getResultList();
//        em.createQuery("select i from Inquiry i join fetch i.member join fetch i.strategy " +
//                "where i.member.name like concat('%', :questionerKeyword, '%')", Inquiry.class)
//                .setParameter("questionerKeyword", inquirySearch.getQuestionerKeyword())
//                .getResultList();
//        em.createQuery("select i from Inquiry i join fetch i.member join fetch i.strategy " +
//                "where i.strategy.trader.name like concat('%', :traderKeyword, '%')", Inquiry.class)
//                .setParameter("traderKeyword", inquirySearch.getTraderKeyword())
//                .getResultList();
        BooleanBuilder predicate = new BooleanBuilder();
        QInquiry inquiry = QInquiry.inquiry;

        if (StringUtils.hasText(inquirySearch.getStrategyKeyword())) {
            predicate.and(inquiry.strategy.name.contains(inquirySearch.getStrategyKeyword()));
        }

        if (StringUtils.hasText(inquirySearch.getQuestionerKeyword())) {
            predicate.and(inquiry.member.name.contains(inquirySearch.getQuestionerKeyword()));
        }

        if (StringUtils.hasText(inquirySearch.getTraderKeyword())) {
            predicate.and(inquiry.strategy.trader.name.contains(inquirySearch.getTraderKeyword()));
        }

        return jpaQueryFactory
                .selectFrom(inquiry)
                .where(predicate)
                .fetch();
    }



//    public List<Inquiry> findByIdAndStrategy(Long id, Strategy strategy) {
//        return em.createQuery("select i from Inquiry i where i.id = :id and i.strategy.id = :strategyId order by i.inquiryRegistrationDate desc", Inquiry.class)
//                .setParameter("id", id)
//                .setParameter("strategyId", strategy.getId())
//                .getResultList();
//    }

//    public List<Inquiry> findByIdAndInquiryStatusAndStrategy(Long id, InquiryStatus inquiryStatus, Strategy strategy) {
//        return em.createQuery("select i from Inquiry i where i.id = :id and i.inquiryStatus = :inquiryStatus and i.strategy.id = :strategyId order by i.inquiryRegistrationDate desc", Inquiry.class)
//                .setParameter("id", id)
//                .setParameter("inquiryStatus", inquiryStatus)
//                .setParameter("strategyId", strategy.getId())
//                .getResultList();
//    }
}
