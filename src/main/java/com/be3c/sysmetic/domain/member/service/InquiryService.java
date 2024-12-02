package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.InquiryAdminListShowRequestDto;
import com.be3c.sysmetic.domain.member.dto.InquiryListShowRequestDto;
import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InquiryService {

    Long saveInquiry(Inquiry inquiry);

    // 문의 단건 조회
    Inquiry findOneInquiry(Long inquiryId);

    // 문의 전체 조회
    Page<Inquiry> findInquiryAll(Integer page);

    // 상태별 문의 조회
    Page<Inquiry> findInquiryByInquiryStatus(InquiryStatus inquiryStatus, Integer page);

    // 일반회원별 문의 조회
    Page<Inquiry> findInquiryByInquirerId(Long inquirerId, Integer page);

    // 일반회원별 상태별 문의 조회
    Page<Inquiry> findInquiryByInquirerIdAndInquiryStatus(Long inquirerId, InquiryStatus inquiryStatus, Integer page);

    // 트레이더별 문의 조회
    Page<Inquiry> findInquiryByTraderId(Long traderId, Integer page);

    // 트레이더별 상태별 문의 조회
    Page<Inquiry> findInquiryByTraderIdAndInquiryStatus(Long traderId, InquiryStatus inquiryStatus, Integer page);

    // 전략 문의 등록 화면 조회
    Strategy findStrategyForInquiryPage(Long strategyId);

    // 등록
    boolean registerInquiry(Long memberId, Long strategyId, String inquiryTitle, String inquiryContent);

    // 수정
    boolean modifyInquiry(Long inquiryId, String inquiryTitle, String inquiryContent);

    // 질문자 삭제
    boolean deleteInquiry(Long inquiryId);

    // 관리자 삭제
    boolean deleteAdminInquiry(Long inquiryId);

    // 관리자 목록 삭제
    Map<Long, String> deleteAdminInquiryList(List<Long> inquiryIdList);

    // 트레이더별 문의id로 조회
    Inquiry findInquiryByTraderIdAndInquiryId(Long inquiryId, Long traderId);

    // 질문자별 문의id로 조회
    Inquiry findInquiryByInquirerIdAndInquiryId(Long inquiryId, Long inquirerId);

    // 관리자 이전 문의 제목 조회
    String adminFindPreviousInquiryTitle(Long inquiryId);

    // 관리자 이전 문의 작성일 조회
    LocalDateTime adminFindPreviousInquiryWriteDate(Long inquiryId);

    // 관리자 다음 문의 제목 조회
    String adminFindNextInquiryTitle(Long inquiryId);

    // 관리자 다음 문의 제목 조회
    LocalDateTime adminFindNextInquiryWriteDate(Long inquiryId);

    // 트레이더 이전 문의 제목 조회
    String traderFindPreviousInquiryTitle(Long inquiryId, Long traderId);

    // 트레이더 이전 문의 작성일 조회
    LocalDateTime traderFindPreviousInquiryWriteDate(Long inquiryId, Long traderId);

    // 트레이더 다음 문의 제목 조회
    String traderFindNextInquiryTitle(Long inquiryId, Long traderId);

    // 트레이더 다음 문의 제목 조회
    LocalDateTime traderFindNextInquiryWriteDate(Long inquiryId, Long traderId);

    // 질문자 이전 문의 제목 조회
    String inquirerFindPreviousInquiryTitle(Long inquiryId, Long inquirerId);

    // 질문자 이전 문의 작성일 조회
    LocalDateTime inquirerFindPreviousInquiryWriteDate(Long inquiryId, Long inquirerId);

    // 질문자 다음 문의 제목 조회
    String inquirerFindNextInquiryTitle(Long inquiryId, Long inquirerId);

    // 질문자 다음 문의 제목 조회
    LocalDateTime inquirerFindNextInquiryWriteDate(Long inquiryId, Long inquirerId);

    // 관리자 검색 조회
    // 전체, 답변 대기, 답변 완료
    // 검색 (전략명, 트레이더, 질문자)
    Page<Inquiry> findInquiriesAdmin(InquiryAdminListShowRequestDto inquiryAdminListShowRequestDto, Integer page);


    // 문의자, 트레이더 검색 조회
    // 정렬 순 셀렉트 박스 (최신순, 전략명)
    // 답변상태 셀렉트 박스 (전체, 답변 대기, 답변 완료)
    Page<Inquiry> findInquiries(InquiryListShowRequestDto inquiryListShowRequestDto, Integer page);
}
