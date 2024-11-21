package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.dto.inquiry.ShowInquiryRequestDto;
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

    public void save(Inquiry inquiry) {
        em.persist(inquiry);
    }

    // 문의 단건 조회
    public Inquiry findOne(Long id) {
        return em.find(Inquiry.class, id);
    }

    // 문의 전체 조회
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

    // 상태별 문의 조회
    public List<Inquiry> findByInquiryStatus(InquiryStatus inquiryStatus, int offset, int limit) {
        if (inquiryStatus == InquiryStatus.ALL) {
            return em.createQuery("select i from Inquiry i order by i.inquiryRegistrationDate desc", Inquiry.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        } else {
            return em.createQuery("select i from Inquiry i where i.inquiryStatus = :inquiryStatus order by i.inquiryRegistrationDate desc", Inquiry.class)
                    .setParameter("inquiryStatus", inquiryStatus)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        }
    }

    public long totalCountInquiryStatus(InquiryStatus inquiryStatus) {
        if (inquiryStatus == InquiryStatus.ALL) {
            return em.createQuery("select count(i) from Inquiry i", Long.class)
                    .getSingleResult();
        } else {
            return em.createQuery("select count(i) from Inquiry i where i.inquiryStatus = :inquiryStatus", Long.class)
                    .setParameter("inquiryStatus", inquiryStatus)
                    .getSingleResult();
        }
    }

    // 일반회원별 문의 조회
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

    // 일반회원별 상태별 문의 조회
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

    // 트레이더별 문의 조회
    public List<Inquiry> findByTraderId(Long traderId, int offset, int limit) {
        return em.createQuery("select i from Inquiry i where i.strategy.trader.id = :traderId order by i.inquiryRegistrationDate desc", Inquiry.class)
                .setParameter("traderId", traderId)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCountTraderId(Long traderId) {
        return em.createQuery("select count(i) from Inquiry i where i.strategy.trader.id = :traderId", Long.class)
                .setParameter("traderId", traderId)
                .getSingleResult();
    }

    // 트레이더별 상태별 문의 조회
    public List<Inquiry> findByTraderIdAndInquiryStatus(Long traderId, InquiryStatus inquiryStatus, int offset, int limit) {
        return em.createQuery("select i from Inquiry i where i.strategy.trader.id = :traderId and i.inquiryStatus = :inquiryStatus order by i.inquiryRegistrationDate desc", Inquiry.class)
                .setParameter("traderId", traderId)
                .setParameter("inquiryStatus", inquiryStatus)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCountTraderIdAndInquiryStatus(Long traderId, InquiryStatus inquiryStatus) {
        return em.createQuery("select count(i) from Inquiry i where i.strategy.trader.id = :traderId and i.inquiryStatus = :inquiryStatus", Long.class)
                .setParameter("traderId", traderId)
                .setParameter("inquiryStatus", inquiryStatus)
                .getSingleResult();
    }

    public void deleteInquiry(Inquiry inquiry) {
        em.remove(inquiry);
    }

    private final JPAQueryFactory jpaQueryFactory;

    public List<Inquiry> dynamicQueryWithBooleanBuilder(ShowInquiryRequestDto showInquiryRequestDto, int offset, int limit) {

        BooleanBuilder predicate = new BooleanBuilder();
        QInquiry inquiry = QInquiry.inquiry;

        Long memberId = showInquiryRequestDto.getMemberId();
        Long traderId = showInquiryRequestDto.getTraderId();
        InquiryStatus tab = showInquiryRequestDto.getTab();
        String searchCondition = showInquiryRequestDto.getSearchCondition();
        String searchKeyword = showInquiryRequestDto.getSearchKeyword();

        // 질문자 별
        if (memberId != null) {
            predicate.and(inquiry.member.id.eq(memberId));
        }

        // 트레이더 별
        if (traderId != null) {
            predicate.and(inquiry.strategy.trader.id.eq(traderId));
        }

        // 전체, 답변 대기, 답변 완료
        if (tab.equals(InquiryStatus.UNCLOSED)) {
            predicate.and(inquiry.inquiryStatus.eq(tab));
        } else if (tab.equals(InquiryStatus.CLOSED)) {
            predicate.and(inquiry.inquiryStatus.eq(tab));
        }

        // 검색 (전략명, 트레이더, 질문자)
        if (searchCondition != null) {
            if (searchCondition.equals("strategy")) {
                if (StringUtils.hasText(searchKeyword)) {
                    predicate.and(inquiry.strategy.name.contains(searchKeyword));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            } else if (searchCondition.equals("trader")) {
                if (StringUtils.hasText(searchKeyword)) {
                    predicate.and(inquiry.strategy.trader.name.contains(searchKeyword));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            } else if (searchCondition.equals("questioner")) {
                if (StringUtils.hasText(searchKeyword)) {
                    predicate.and(inquiry.member.name.contains(searchKeyword));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            }
        }

        return jpaQueryFactory
                .selectFrom(inquiry)
                .where(predicate)
                .orderBy(inquiry.inquiryRegistrationDate.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    public long totalCountDynamicQueryWithBooleanBuilder(ShowInquiryRequestDto showInquiryRequestDto) {
        BooleanBuilder predicate = new BooleanBuilder();
        QInquiry inquiry = QInquiry.inquiry;

        Long memberId = showInquiryRequestDto.getMemberId();
        Long traderId = showInquiryRequestDto.getTraderId();
        InquiryStatus tab = showInquiryRequestDto.getTab();
        String searchCondition = showInquiryRequestDto.getSearchCondition();
        String searchKeyword = showInquiryRequestDto.getSearchKeyword();

        // 질문자 별
        if (memberId != null) {
            predicate.and(inquiry.member.id.eq(memberId));
        }

        // 트레이더 별
        if (traderId != null) {
            predicate.and(inquiry.strategy.trader.id.eq(traderId));
        }

        // 전체, 답변 대기, 답변 완료
        if (tab.equals(InquiryStatus.UNCLOSED)) {
            predicate.and(inquiry.inquiryStatus.eq(tab));
        } else if (tab.equals(InquiryStatus.CLOSED)) {
            predicate.and(inquiry.inquiryStatus.eq(tab));
        }

        // 검색 (전략명, 트레이더, 질문자)
        if (searchCondition != null) {
            if (searchCondition.equals("전략명")) {
                if (StringUtils.hasText(searchKeyword)) {
                    predicate.and(inquiry.strategy.name.contains(searchKeyword));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            } else if (searchCondition.equals("트레이더")) {
                if (StringUtils.hasText(searchKeyword)) {
                    predicate.and(inquiry.strategy.trader.name.contains(searchKeyword));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            } else if (searchCondition.equals("질문자")) {
                if (StringUtils.hasText(searchKeyword)) {
                    predicate.and(inquiry.member.name.contains(searchKeyword));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            }
        }

        return jpaQueryFactory
                .select(inquiry.count())
                .from(inquiry)
                .where(predicate)
                .fetchOne();
    }
}
