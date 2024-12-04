package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.dto.InquiryAdminListShowRequestDto;
import com.be3c.sysmetic.domain.member.dto.InquiryListShowRequestDto;
import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.entity.QInquiry;
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
public class InquiryRepositoryImpl implements InquiryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QInquiry inquiry = QInquiry.inquiry;

    public Page<Inquiry> adminInquirySearchWithBooleanBuilder(InquiryAdminListShowRequestDto inquiryAdminListShowRequestDto, Pageable pageable) {

        BooleanBuilder predicate = new BooleanBuilder();

        InquiryStatus tab = inquiryAdminListShowRequestDto.getTab();
        String searchType = inquiryAdminListShowRequestDto.getSearchType();
        String searchText = inquiryAdminListShowRequestDto.getSearchText();

        // 전체, 답변 대기, 답변 완료
        if (tab.equals(InquiryStatus.unclosed)) {
            predicate.and(inquiry.inquiryStatus.eq(tab));
        } else if (tab.equals(InquiryStatus.closed)) {
            predicate.and(inquiry.inquiryStatus.eq(tab));
        }

        // 검색 (전략명, 트레이더, 질문자)
        if (StringUtils.hasText(searchText)) {
            if (searchType.equals("strategy")) {
                predicate.and(inquiry.strategy.name.contains(searchText));
            } else if (searchType.equals("trader")) {
                predicate.and(inquiry.strategy.trader.nickname.contains(searchText));
            } else if (searchType.equals("inquirer")) {
                predicate.and(inquiry.inquirer.nickname.contains(searchText));
            }
        }

        QueryResults<Inquiry> results = jpaQueryFactory
                .selectFrom(inquiry)
                .where(predicate)
                .orderBy(inquiry.id.desc()) // 따로 해서 최적화 가능
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Inquiry> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }


    public Page<Inquiry> inquirySearchWithBooleanBuilder(InquiryListShowRequestDto inquiryListShowRequestDto, Pageable pageable) {

        BooleanBuilder predicate = new BooleanBuilder();

        Long inquirerId = inquiryListShowRequestDto.getInquirerId();
        Long traderId = inquiryListShowRequestDto.getTraderId();
        String sort = inquiryListShowRequestDto.getSort();
        InquiryStatus tab = inquiryListShowRequestDto.getTab();

        // 질문자 별
        if (inquirerId != null) {
            predicate.and(inquiry.inquirer.id.eq(inquirerId));
        }

        // 트레이더 별
        if (traderId != null) {
            predicate.and(inquiry.traderId.eq(traderId));
        }

        // 전체, 답변 대기, 답변 완료
        if (tab.equals(InquiryStatus.unclosed)) {
            predicate.and(inquiry.inquiryStatus.eq(tab));
        } else if (tab.equals(InquiryStatus.closed)) {
            predicate.and(inquiry.inquiryStatus.eq(tab));
        }

        QueryResults<Inquiry> results;
        // 정렬순 별
        if (sort.equals("registrationDate")) {
            results = jpaQueryFactory
                    .selectFrom(inquiry)
                    .where(predicate)
                    .orderBy(inquiry.inquiryRegistrationDate.desc()) // 따로 해서 최적화 가능
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetchResults();
        } else if (sort.equals("strategyName")) {
            results = jpaQueryFactory
                    .selectFrom(inquiry)
                    .where(predicate)
                    .orderBy(inquiry.strategy.name.asc(), inquiry.inquiryRegistrationDate.asc()) // 따로 해서 최적화 가능
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetchResults();
        } else {
            throw new IllegalArgumentException("정렬순을 지정하세요");
        }


        List<Inquiry> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
}
