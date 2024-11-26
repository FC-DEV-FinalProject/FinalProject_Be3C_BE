package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.Notice;
import com.be3c.sysmetic.domain.member.entity.QNotice;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QNotice notice = QNotice.notice;

    @Override
    public Page<Notice> adminNoticeSearchWithBooleanBuilder(String searchType, String searchText, Pageable pageable) {

        BooleanBuilder predicate = new BooleanBuilder();

        // 검색 (제목, 내용, 제목+내용, 작성자)
        if (searchText != null) {
            if (searchType.equals("title")) {
                if (StringUtils.hasText(searchText)) {
                    predicate.and(notice.noticeTitle.contains(searchText));
                }
            } else if (searchType.equals("content")) {
                if (StringUtils.hasText(searchText)) {
                    predicate.and(notice.noticeContent.contains(searchText));
                }
            } else if (searchType.equals("all")) {
                if (StringUtils.hasText(searchText)) {
                    predicate.andAnyOf(notice.noticeTitle.contains(searchText), notice.noticeContent.contains(searchText));
                }
            } else if (searchType.equals("writer")) {
                if (StringUtils.hasText(searchText)) {
                    predicate.and(notice.writer.name.contains(searchText));
                }
            }
        }

        QueryResults<Notice> results = jpaQueryFactory
                .selectFrom(notice)
                .where(predicate)
                .orderBy(notice.writeDate.desc()) // 따로 해서 최적화 가능
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Notice> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }


    @Override
    public Page<Notice> noticeSearchWithBooleanBuilder(String searchText, Pageable pageable) {

        BooleanBuilder predicate = new BooleanBuilder();

        if (StringUtils.hasText(searchText)) {
            predicate.andAnyOf(notice.noticeTitle.contains(searchText), notice.noticeContent.contains(searchText));
        }

        QueryResults<Notice> results = jpaQueryFactory
                .selectFrom(notice)
                .where(predicate)
                .orderBy(notice.writeDate.desc()) // 따로 해서 최적화 가능
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Notice> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
}
