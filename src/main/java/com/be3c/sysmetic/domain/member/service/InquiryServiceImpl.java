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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.be3c.sysmetic.domain.member.message.NoticeDeleteFailMessage.NOT_FOUND_INQUIRY;
import static com.be3c.sysmetic.domain.member.message.NoticeDeleteFailMessage.NOT_FOUND_NOTICE;

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
        return inquiryRepository.findById(inquiryId).orElseThrow(() -> new EntityNotFoundException("문의가 없습니다."));
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
        return strategyRepository.findById(strategyId).orElseThrow(() -> new EntityNotFoundException("전략이 없습니다."));
    }

    // 등록
    @Override
    @Transactional
    public boolean registerInquiry(Long memberId, Long strategyId, String inquiryTitle, String inquiryContent) {

        Strategy strategy = strategyRepository.findById(strategyId).orElseThrow(() -> new EntityNotFoundException("전략이 없습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("회원이 없습니다."));

        Inquiry inquiry = Inquiry.createInquiry(strategy, member, inquiryTitle, inquiryContent);

        inquiryRepository.save(inquiry);

        return true;
    }

    // 수정
    @Override
    @Transactional
    public boolean modifyInquiry(Long inquiryId, String inquiryTitle, String inquiryContent) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new EntityNotFoundException("문의가 없습니다."));
        if (inquiry.getInquiryStatus() == InquiryStatus.unclosed) {
            inquiry.setInquiryTitle(inquiryTitle);
            inquiry.setInquiryContent(inquiryContent);
            inquiryRepository.save(inquiry);
        } else {
            throw new IllegalStateException("답변이 등록된 문의는 수정할 수 없습니다.");
        }

        return true;
    }

    // 질문자 삭제
    @Override
    @Transactional
    public boolean deleteInquiry(Long inquiryId) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new EntityNotFoundException("문의가 없습니다."));

        if (inquiry.getInquiryStatus() == InquiryStatus.unclosed) {
            inquiryRepository.delete(inquiry);
        } else {
            throw new IllegalStateException("답변이 등록된 문의는 삭제할 수 없습니다.");
        }

        return true;
    }

    // 관리자 삭제
    @Override
    @Transactional
    public boolean deleteAdminInquiry(Long inquiryId) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new EntityNotFoundException("문의가 없습니다."));

        inquiryRepository.delete(inquiry);

        return true;
    }


    // 관리자 목록 삭제
    @Override
    @Transactional
    public Map<Long, String> deleteAdminInquiryList(List<Long> inquiryIdList) {

        if (inquiryIdList == null || inquiryIdList.isEmpty()) {
            throw new IllegalArgumentException("문의가 한 개도 선택되지 않았습니다.");
        }

        Map<Long, String> failDelete = new HashMap<>();

        for (Long inquiryId : inquiryIdList) {
            try {
                inquiryRepository.findById(inquiryId);
            }
            catch (EntityNotFoundException e) {
                failDelete.put(inquiryId, NOT_FOUND_INQUIRY.getMessage());
            }
        }

        inquiryRepository.bulkDelete(inquiryIdList);

        return failDelete;
    }


    // 트레이더별 문의id로 조회
    @Override
    public Inquiry findInquiryByTraderIdAndInquiryId(Long inquiryId, Long traderId) {
        List<Inquiry> inquiryList = inquiryRepository.findInquiryByTraderIdAndInquiryId(inquiryId, traderId, PageRequest.of(0, 1));

        if (inquiryList.isEmpty()) {
            throw new EntityNotFoundException("문의를 찾을 수 없습니다.");
        } else {
            return inquiryList.get(0);
        }
    }

    // 질문자별 문의id로 조회
    @Override
    public Inquiry findInquiryByInquirerIdAndInquiryId(Long inquiryId, Long inquirerId) {
        List<Inquiry> inquiryList = inquiryRepository.findInquiryByInquirerIdAndInquiryId(inquiryId, inquirerId, PageRequest.of(0, 1));

        if (inquiryList.isEmpty()) {
            throw new EntityNotFoundException("문의를 찾을 수 없습니다.");
        } else {
            return inquiryList.get(0);
        }
    }

    // 관리자 이전 문의 제목 조회
    @Override
    public String adminFindPreviousInquiryTitle(Long inquiryId) {
        List<Inquiry> previousInquiryList = inquiryRepository.adminFindPreviousInquiry(inquiryId, PageRequest.of(0, 1));

        String previousInquiryTitle;
        if (previousInquiryList.isEmpty()) {
            previousInquiryTitle = null;
        } else {
            Inquiry previousInquiry = previousInquiryList.get(0);
            previousInquiryTitle = previousInquiry.getInquiryTitle();
        }

        return previousInquiryTitle;
    }

    // 관리자 이전 문의 작성일 조회
    @Override
    public LocalDateTime adminFindPreviousInquiryWriteDate(Long inquiryId) {
        List<Inquiry> previousInquiryList = inquiryRepository.adminFindPreviousInquiry(inquiryId, PageRequest.of(0, 1));

        LocalDateTime previousInquiryWriteDate;
        if (previousInquiryList.isEmpty()) {
            previousInquiryWriteDate = null;
        } else {
            Inquiry previousInquiry = previousInquiryList.get(0);
            previousInquiryWriteDate = previousInquiry.getInquiryRegistrationDate();
        }

        return previousInquiryWriteDate;
    }

    // 관리자 다음 문의 제목 조회
    @Override
    public String adminFindNextInquiryTitle(Long inquiryId) {
        List<Inquiry> nextInquiryList = inquiryRepository.adminFindNextInquiry(inquiryId, PageRequest.of(0, 1));

        String nextInquiryTitle;
        if (nextInquiryList.isEmpty()) {
            nextInquiryTitle = null;
        } else {
            Inquiry previousInquiry = nextInquiryList.get(0);
            nextInquiryTitle = previousInquiry.getInquiryTitle();
        }

        return nextInquiryTitle;
    }

    // 관리자 다음 문의 제목 조회
    @Override
    public LocalDateTime adminFindNextInquiryWriteDate(Long inquiryId) {
        List<Inquiry> nextInquiryList = inquiryRepository.adminFindNextInquiry(inquiryId, PageRequest.of(0, 1));

        LocalDateTime nextInquiryWriteDate;
        if (nextInquiryList.isEmpty()) {
            nextInquiryWriteDate = null;
        } else {
            Inquiry previousInquiry = nextInquiryList.get(0);
            nextInquiryWriteDate = previousInquiry.getInquiryRegistrationDate();
        }

        return nextInquiryWriteDate;
    }

    // 트레이더 이전 문의 제목 조회
    @Override
    public String traderFindPreviousInquiryTitle(Long inquiryId, Long traderId) {
        List<Inquiry> previousInquiryList = inquiryRepository.traderFindPreviousInquiry(inquiryId, traderId, PageRequest.of(0, 1));

        String previousInquiryTitle;
        if (previousInquiryList.isEmpty()) {
            previousInquiryTitle = null;
        } else {
            Inquiry previousInquiry = previousInquiryList.get(0);
            previousInquiryTitle = previousInquiry.getInquiryTitle();
        }

        return previousInquiryTitle;
    }

    // 트레이더 이전 문의 작성일 조회
    @Override
    public LocalDateTime traderFindPreviousInquiryWriteDate(Long inquiryId, Long traderId) {
        List<Inquiry> previousInquiryList = inquiryRepository.traderFindPreviousInquiry(inquiryId, traderId, PageRequest.of(0, 1));

        LocalDateTime previousInquiryWriteDate;
        if (previousInquiryList.isEmpty()) {
            previousInquiryWriteDate = null;
        } else {
            Inquiry previousInquiry = previousInquiryList.get(0);
            previousInquiryWriteDate = previousInquiry.getInquiryRegistrationDate();
        }

        return previousInquiryWriteDate;
    }

    // 트레이더 다음 문의 제목 조회
    @Override
    public String traderFindNextInquiryTitle(Long inquiryId, Long traderId) {
        List<Inquiry> nextInquiryList = inquiryRepository.traderFindNextInquiry(inquiryId, traderId, PageRequest.of(0, 1));

        String nextInquiryTitle;
        if (nextInquiryList.isEmpty()) {
            nextInquiryTitle = null;
        } else {
            Inquiry previousInquiry = nextInquiryList.get(0);
            nextInquiryTitle = previousInquiry.getInquiryTitle();
        }

        return nextInquiryTitle;
    }

    // 트레이더 다음 문의 제목 조회
    @Override
    public LocalDateTime traderFindNextInquiryWriteDate(Long inquiryId, Long traderId) {
        List<Inquiry> nextInquiryList = inquiryRepository.traderFindNextInquiry(inquiryId, traderId, PageRequest.of(0, 1));

        LocalDateTime nextInquiryWriteDate;
        if (nextInquiryList.isEmpty()) {
            nextInquiryWriteDate = null;
        } else {
            Inquiry previousInquiry = nextInquiryList.get(0);
            nextInquiryWriteDate = previousInquiry.getInquiryRegistrationDate();
        }

        return nextInquiryWriteDate;
    }

    // 질문자 이전 문의 제목 조회
    @Override
    public String inquirerFindPreviousInquiryTitle(Long inquiryId, Long inquirerId) {
        List<Inquiry> previousInquiryList = inquiryRepository.inquirerFindPreviousInquiry(inquiryId, inquirerId, PageRequest.of(0, 1));

        String previousInquiryTitle;
        if (previousInquiryList.isEmpty()) {
            previousInquiryTitle = null;
        } else {
            Inquiry previousInquiry = previousInquiryList.get(0);
            previousInquiryTitle = previousInquiry.getInquiryTitle();
        }

        return previousInquiryTitle;
    }

    // 질문자 이전 문의 작성일 조회
    @Override
    public LocalDateTime inquirerFindPreviousInquiryWriteDate(Long inquiryId, Long inquirerId) {
        List<Inquiry> previousInquiryList = inquiryRepository.inquirerFindPreviousInquiry(inquiryId, inquirerId, PageRequest.of(0, 1));

        LocalDateTime previousInquiryWriteDate;
        if (previousInquiryList.isEmpty()) {
            previousInquiryWriteDate = null;
        } else {
            Inquiry previousInquiry = previousInquiryList.get(0);
            previousInquiryWriteDate = previousInquiry.getInquiryRegistrationDate();
        }

        return previousInquiryWriteDate;
    }

    // 질문자 다음 문의 제목 조회
    @Override
    public String inquirerFindNextInquiryTitle(Long inquiryId, Long inquirerId) {
        List<Inquiry> nextInquiryList = inquiryRepository.inquirerFindNextInquiry(inquiryId, inquirerId, PageRequest.of(0, 1));

        String nextInquiryTitle;
        if (nextInquiryList.isEmpty()) {
            nextInquiryTitle = null;
        } else {
            Inquiry previousInquiry = nextInquiryList.get(0);
            nextInquiryTitle = previousInquiry.getInquiryTitle();
        }

        return nextInquiryTitle;
    }

    // 질문자 다음 문의 제목 조회
    @Override
    public LocalDateTime inquirerFindNextInquiryWriteDate(Long inquiryId, Long inquirerId) {
        List<Inquiry> nextInquiryList = inquiryRepository.inquirerFindNextInquiry(inquiryId, inquirerId, PageRequest.of(0, 1));

        LocalDateTime nextInquiryWriteDate;
        if (nextInquiryList.isEmpty()) {
            nextInquiryWriteDate = null;
        } else {
            Inquiry previousInquiry = nextInquiryList.get(0);
            nextInquiryWriteDate = previousInquiry.getInquiryRegistrationDate();
        }

        return nextInquiryWriteDate;
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
