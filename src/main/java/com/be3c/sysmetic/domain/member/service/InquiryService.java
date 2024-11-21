package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.inquiry.ShowInquiryRequestDto;
import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.InquiryRepository;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;
    private final StrategyRepository strategyRepository;

    @Transactional
    public Long saveInquiry(Inquiry inquiry) {
        inquiryRepository.save(inquiry);
        return inquiry.getId();
    }

    // 문의 단건 조회
    public Inquiry findOneInquiry(Long inquiryId) {
        return inquiryRepository.findOne(inquiryId);
    }

    // 문의 전체 조회
    public List<Inquiry> findInquiryAll(int offset, int limit) {
        return inquiryRepository.findAll(offset, limit);
    }

    public long totalCountAll() {
        return inquiryRepository.totalCountAll();
    }

    // 상태별 문의 조회
    public List<Inquiry> findInquiryByStatus(InquiryStatus inquiryStatus, int offset, int limit) {
        return inquiryRepository.findByInquiryStatus(inquiryStatus, offset, limit);
    }

    public long totalCountByStatus(InquiryStatus inquiryStatus) {
        return inquiryRepository.totalCountInquiryStatus(inquiryStatus);
    }

    // 일반회원별 문의 조회
    public List<Inquiry> findInquiryByMember(Long memberId, int offset, int limit) {
        return inquiryRepository.findByMemberId(memberId, offset, limit);
    }

    public long totalCountByMember(Long memberId) {
        return inquiryRepository.totalCountMemberId(memberId);
    }

    // 일반회원별 상태별 문의 조회
    public List<Inquiry> findInquiryByMemberAndStatus(Long memberId, InquiryStatus inquiryStatus, int offset, int limit) {
        return inquiryRepository.findByMemberIdAndInquiryStatus(memberId, inquiryStatus, offset, limit);
    }

    public long totalCountByMemberAndStatus(Long memberId, InquiryStatus inquiryStatus) {
        return inquiryRepository.totalCountMemberIdAndInquiryStatus(memberId, inquiryStatus);
    }

    // 트레이더별 문의 조회
    public List<Inquiry> findInquiryByTrader(Long traderId, int offset, int limit) {
        return inquiryRepository.findByTraderId(traderId, offset, limit);
    }

    public long totalCountByTrader(Long traderId) {
        return inquiryRepository.totalCountTraderId(traderId);
    }

    // 트레이더별 상태별 문의 조회
    public List<Inquiry> findInquiryByTraderAndStatus(Long traderId, InquiryStatus inquiryStatus, int offset, int limit) {
        return inquiryRepository.findByTraderIdAndInquiryStatus(traderId, inquiryStatus, offset, limit);
    }

    public long totalCountByTraderAndStatus(Long traderId, InquiryStatus inquiryStatus) {
        return inquiryRepository.totalCountTraderIdAndInquiryStatus(traderId, inquiryStatus);
    }

    // 등록
    @Transactional
    public Long registerInquiry(Long memberId, Long strategyId, String inquiryTitle, String inquiryContent) {
        Strategy strategy = strategyRepository.findById(strategyId).get(); // .orElseThrow() 예외처리
        Member member = memberRepository.findById(memberId).get();

        Inquiry inquiry = Inquiry.createInquiry(strategy, member, inquiryTitle, inquiryContent);

        inquiryRepository.save(inquiry);

        return inquiry.getId();
    }

    // 수정
    @Transactional
    public void modifyInquiry(Long inquiryId, String inquiryTitle, String inquiryContent) {

        Inquiry inquiry = inquiryRepository.findOne(inquiryId);

        if (inquiry.getInquiryStatus() == InquiryStatus.UNCLOSED) {
            inquiry.setInquiryTitle(inquiryTitle);
            inquiry.setInquiryContent(inquiryContent);
            inquiryRepository.save(inquiry);
        } else {
            throw new IllegalStateException("답변이 등록된 문의는 수정할 수 없습니다.");
        }
    }

    // 삭제
    @Transactional
    public void deleteInquiry(Long inquiryId) {

        Inquiry inquiry = inquiryRepository.findOne(inquiryId);

        if (inquiry.getInquiryStatus() == InquiryStatus.UNCLOSED) {
            inquiryRepository.deleteInquiry(inquiry);
        } else {
            throw new IllegalStateException("답변이 등록된 문의는 삭제할 수 없습니다.");
        }
    }

    // 검색
    public List<Inquiry> findInquiresByStrategyQuestionerTrader(ShowInquiryRequestDto showInquiryRequestDto, int offset, int limit) {
        return inquiryRepository.dynamicQueryWithBooleanBuilder(showInquiryRequestDto, offset, limit);
    }

    public long totalCountByStrategyQuestionerTrader(ShowInquiryRequestDto showInquiryRequestDto) {
        return inquiryRepository.totalCountDynamicQueryWithBooleanBuilder(showInquiryRequestDto);
    }
}
