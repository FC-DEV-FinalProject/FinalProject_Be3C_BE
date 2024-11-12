package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.InquiryRepository;
import com.be3c.sysmetic.domain.member.repository.InquirySearch;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InquiryService {

    private final EntityManager em;

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
    public List<Inquiry> findAllInquiries(int offset, int limit) {
        return inquiryRepository.findAll(offset, limit);
    }

    // 상태별 문의 조회
    public List<Inquiry> findStatusInquires(InquiryStatus inquiryStatus, int offset, int limit) {
        return inquiryRepository.findByInquiryStatus(inquiryStatus, offset, limit);
    }

    // 내(회원별) 문의 조회
    public List<Inquiry> findMemberInquiries(Long memberId, int offset, int limit) {
        return inquiryRepository.findByMemberId(memberId, offset, limit);
    }

    // 내(회원별) 상태별 문의 조회
    public List<Inquiry> findMemberStatusInquires(Long memberId, InquiryStatus inquiryStatus, int offset, int limit) {
        return inquiryRepository.findByMemberIdAndInquiryStatus(memberId, inquiryStatus, offset, limit);
    }

    // 등록
    @Transactional
    public Long registerInquiry(Long memberId, Long strategyId, String inquiryTitle, String inquiryContent) {
        Strategy strategy = strategyRepository.findOne(strategyId);
        Member member = memberRepository.findOne(memberId);

        Inquiry inquiry = Inquiry.createInquiry(strategy, member, inquiryTitle, inquiryContent);

        inquiryRepository.save(inquiry);

        return inquiry.getId();
    }

    // 수정
    @Transactional
    public void modifyInquiry(Long inquiryId, String inquiryTitle, String inquiryContent) {
        Inquiry inquiry = em.find(Inquiry.class, inquiryId);

        if (inquiry.getInquiryStatus() == InquiryStatus.INCOMPLETE) {
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
        Inquiry inquiry = em.find(Inquiry.class, inquiryId);

        if (inquiry.getInquiryStatus() == InquiryStatus.INCOMPLETE) {
            inquiryRepository.deleteInquiry(inquiry);
        } else {
            throw new IllegalStateException("답변이 등록된 문의는 삭제할 수 없습니다.");
        }
    }

    // 검색
    public List<Inquiry> findInquiresByStrategyQuestionerTrader(InquirySearch inquirySearch) {
        return inquiryRepository.dynamicQueryWithBooleanBuilder(inquirySearch);
    }
}
