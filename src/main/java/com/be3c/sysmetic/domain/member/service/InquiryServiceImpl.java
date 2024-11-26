package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.InquiryAdminListShowRequestDto;
import com.be3c.sysmetic.domain.member.dto.InquiryListShowRequestDto;
import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.InquiryRepository;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;
    private final StrategyRepository strategyRepository;

    @Override
    @Transactional
    public Long saveInquiry(Inquiry inquiry) {
        inquiryRepository.save(inquiry);
        return inquiry.getId();
    }

    // 문의 단건 조회
    @Override
    public Inquiry findOneInquiry(Long inquiryId) {
        return inquiryRepository.findById(inquiryId).orElseThrow(EntityNotFoundException::new);
    }

    // 문의 전체 조회
    @Override
    public Page<Inquiry> findInquiryAll(Integer page) {
        return inquiryRepository.findAll(PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "inquiryRegistrationDate")));
    }

    // 상태별 문의 조회
    @Override
    public Page<Inquiry> findInquiryByInquiryStatus(InquiryStatus inquiryStatus, Integer page) {
        return inquiryRepository.findByInquiryStatus(inquiryStatus, PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "inquiryRegistrationDate")));
    }

    // 일반회원별 문의 조회
    @Override
    public Page<Inquiry> findInquiryByInquirerId(Long inquirerId, Integer page) {
        return inquiryRepository.findByInquirerId(inquirerId, PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "inquiryRegistrationDate")));
    }

    // 일반회원별 상태별 문의 조회
    @Override
    public Page<Inquiry> findInquiryByInquirerIdAndInquiryStatus(Long inquirerId, InquiryStatus inquiryStatus, Integer page) {
        return inquiryRepository.findByInquirerIdAndInquiryStatus(inquirerId, inquiryStatus, PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "inquiryRegistrationDate")));
    }

    // 트레이더별 문의 조회
    @Override
    public Page<Inquiry> findInquiryByTraderId(Long traderId, Integer page) {
        return inquiryRepository.findByTraderId(traderId, PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "inquiryRegistrationDate")));
    }

    // 트레이더별 상태별 문의 조회
    @Override
    public Page<Inquiry> findInquiryByTraderIdAndInquiryStatus(Long traderId, InquiryStatus inquiryStatus, Integer page) {
        return inquiryRepository.findByTraderIdAndInquiryStatus(traderId, inquiryStatus, PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "inquiryRegistrationDate")));
    }

    // 전략 문의 등록 화면 조회
    @Override
    public Strategy findStrategyForInquiryPage(Long strategyId) {
        return strategyRepository.findById(strategyId).orElseThrow(EntityNotFoundException::new);
    }

    // 등록
    @Override
    @Transactional
    public Long registerInquiry(Long memberId, Long strategyId, String inquiryTitle, String inquiryContent) {

        Strategy strategy = strategyRepository.findById(strategyId).orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new); // 등록할 전략을 찾지 못했을 때 : NOT_FOUND

        Inquiry inquiry = Inquiry.createInquiry(strategy, member, inquiryTitle, inquiryContent);

        inquiryRepository.save(inquiry);

        return inquiry.getId();
    }

    // 수정
    @Override
    @Transactional
    public void modifyInquiry(Long inquiryId, String inquiryTitle, String inquiryContent) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(EntityNotFoundException::new); // 등록할 전략을 찾지 못했을 때 : NOT_FOUND

        if (inquiry.getInquiryStatus() == InquiryStatus.unclosed) {
            inquiry.setInquiryTitle(inquiryTitle);
            inquiry.setInquiryContent(inquiryContent);
            inquiryRepository.save(inquiry);
        } else {
            throw new IllegalStateException("답변이 등록된 문의는 수정할 수 없습니다.");
        }
    }

    // 질문자 삭제
    @Override
    @Transactional
    public void deleteInquiry(Long inquiryId) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(EntityNotFoundException::new); // 등록할 전략을 찾지 못했을 때 : NOT_FOUND

        if (inquiry.getInquiryStatus() == InquiryStatus.unclosed) {
            inquiryRepository.delete(inquiry);
        } else {
            throw new IllegalStateException("답변이 등록된 문의는 삭제할 수 없습니다."); // 이미 관심 전략에 등록된 전략일 때 : BAD_REQUEST
        }
    }

    // 관리자 삭제
    @Override
    @Transactional
    public void deleteAdminInquiry(Long inquiryId) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(EntityNotFoundException::new); // 등록할 전략을 찾지 못했을 때 : NOT_FOUND

        inquiryRepository.delete(inquiry);
    }


    // 관리자 목록 삭제
    @Override
    @Transactional
    public void deleteAdminInquiryList(List<Long> inquiryIdList) {

        inquiryRepository.bulkDelete(inquiryIdList);
    }

    // 관리자 검색 조회
    // 전체, 답변 대기, 답변 완료
    // 검색 (전략명, 트레이더, 질문자)
    public Page<Inquiry> findInquiresAdmin(InquiryAdminListShowRequestDto inquiryAdminListShowRequestDto, Integer page) {

        return inquiryRepository.adminInquirySearchWithBooleanBuilder(inquiryAdminListShowRequestDto, PageRequest.of(page, 10));
    }

    // 문의자, 트레이더 검색 조회
    // 정렬 순 셀렉트 박스 (최신순, 전략명)
    // 답변상태 셀렉트 박스 (전체, 답변 대기, 답변 완료)
    public Page<Inquiry> findInquires(InquiryListShowRequestDto inquiryListShowRequestDto, Integer page) {

        return inquiryRepository.inquirySearchWithBooleanBuilder(inquiryListShowRequestDto, PageRequest.of(page, 10));
    }
}
