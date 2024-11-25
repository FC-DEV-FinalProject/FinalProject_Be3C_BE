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

        // 검색 (제목, 내용, 제목+내용, 작성자)
        if (searchType != null) {
            if (searchType.equals("title")) {
                if (StringUtils.hasText(searchText)) {
                    predicate.and(notice.noticeTitle.contains(searchText));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            } else if (searchType.equals("content")) {
                if (StringUtils.hasText(searchText)) {
                    predicate.and(notice.noticeContent.contains(searchText));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            } else if (searchType.equals("all")) {
                if (StringUtils.hasText(searchText)) {
                    predicate.andAnyOf(notice.noticeTitle.contains(searchText), notice.noticeContent.contains(searchText));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            } else if (searchType.equals("writer")) {
                if (StringUtils.hasText(searchText)) {
                    predicate.and(notice.writer.name.contains(searchText));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            }
        }

        return jpaQueryFactory
                .selectFrom(notice)
                .where(predicate)
                .orderBy(notice.writeDate.desc())
                .fetch();
    }

    @Override
    public Long adminNoticeCountWithBooleanBuilder(String searchType, String searchText) {

        BooleanBuilder predicate = new BooleanBuilder();

        // 검색 (제목, 내용, 제목+내용, 작성자)
        if (searchType != null) {
            if (searchType.equals("title")) {
                if (StringUtils.hasText(searchText)) {
                    predicate.and(notice.noticeTitle.contains(searchText));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            } else if (searchType.equals("content")) {
                if (StringUtils.hasText(searchText)) {
                    predicate.and(notice.noticeContent.contains(searchText));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            } else if (searchType.equals("all")) {
                if (StringUtils.hasText(searchText)) {
                    predicate.andAnyOf(notice.noticeTitle.contains(searchText), notice.noticeContent.contains(searchText));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            } else if (searchType.equals("writer")) {
                if (StringUtils.hasText(searchText)) {
                    predicate.and(notice.writer.name.contains(searchText));
                } else {
                    throw new IllegalArgumentException("검색어를 입력하세요.");
                }
            }
        }

        return jpaQueryFactory
                .select(notice.count())
                .from(notice)
                .where(predicate)
                .fetchOne();
    }


    @Override
    public List<Notice> noticeSearchWithBooleanBuilder(String searchText) {

        BooleanBuilder predicate = new BooleanBuilder();

        if (StringUtils.hasText(searchText)) {
            predicate.andAnyOf(notice.noticeTitle.contains(searchText), notice.noticeContent.contains(searchText));
        } else {
            throw new IllegalArgumentException("검색어를 입력하세요.");
        }

        return jpaQueryFactory
                .selectFrom(notice)
                .where(predicate)
                .orderBy(notice.writeDate.desc())
                .fetch();
    }

    @Override
    public Long noticeCountWithBooleanBuilder(String searchText) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (StringUtils.hasText(searchText)) {
            predicate.andAnyOf(notice.noticeTitle.contains(searchText), notice.noticeContent.contains(searchText));
        } else {
            throw new IllegalArgumentException("검색어를 입력하세요.");
        }

        return jpaQueryFactory
                .select(notice.count())
                .from(notice)
                .where(predicate)
                .fetchOne();
    }
}
