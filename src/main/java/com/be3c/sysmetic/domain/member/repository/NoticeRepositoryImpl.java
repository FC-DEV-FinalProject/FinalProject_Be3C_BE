package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.Notice;
import com.be3c.sysmetic.domain.member.entity.QNotice;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
//import com.be3c.sysmetic.domain.member.entity.

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QNotice notice = QNotice.notice;

    @Override
    public List<Notice> adminNoticeSearchWithBooleanBuilder(String searchType, String searchText) {

        BooleanBuilder predicate = new BooleanBuilder();

        // 검색 (전략명, 트레이더, 질문자)
        if (searchType != null) {
            if (searchType.equals("strategy")) {
                if (StringUtils.hasText(searchText)) {
                    predicate.and(inquiry.strategy.name.contains(searchText));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            } else if (searchType.equals("trader")) {
                if (StringUtils.hasText(searchText)) {
                    predicate.and(inquiry.strategy.trader.name.contains(searchText));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            } else if (searchType.equals("questioner")) {
                if (StringUtils.hasText(searchText)) {
                    predicate.and(inquiry.member.name.contains(searchText));
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
    @Override
    public Long adminNoticeCountWithBooleanBuilder(String searchType, String searchText) {

//        return jpaQueryFactory
//                .select(inquiry.count())
//                .from(inquiry)
//                .where(predicate)
//                .fetchOne();
        return null;
    }

    @Override
    public List<Notice> noticeSearchWithBooleanBuilder(String searchText) {
//        BooleanBuilder predicate = new BooleanBuilder();
//
//        if (name != null) {
//            predicate.and(notice.restaurantName.eq(name));
//        }
//
//        return jpaQueryFactory
//                .selectFrom(notice)
//                .where(predicate)
//                .fetch();
        return null;
    }

    @Override
    public Long noticeCountWithBooleanBuilder(String searchText) {
        return null;
    }
}
