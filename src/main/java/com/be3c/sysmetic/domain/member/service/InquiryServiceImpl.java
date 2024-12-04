package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.*;
import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryAnswer;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.exception.MemberBadRequestException;
import com.be3c.sysmetic.domain.member.exception.MemberExceptionMessage;
import com.be3c.sysmetic.domain.member.repository.InquiryAnswerRepository;
import com.be3c.sysmetic.domain.member.repository.InquiryRepository;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.StockListDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.StrategyRepository;
import com.be3c.sysmetic.domain.strategy.util.StockGetter;
import com.be3c.sysmetic.global.util.SecurityUtils;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.service.FileService;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private final SecurityUtils securityUtils;

    private final InquiryRepository inquiryRepository;
    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final MemberRepository memberRepository;
    private final StrategyRepository strategyRepository;

    private final FileService fileService;
    private final StockGetter stockGetter;

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
            throw new EntityNotFoundException("문의가 한 개도 선택되지 않았습니다.");
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


    // 관리자 검색 조회
    // 전체, 답변 대기, 답변 완료
    // 검색 (전략명, 트레이더, 질문자)
    @Override
    public Page<Inquiry> findInquiriesAdmin(InquiryAdminListShowRequestDto inquiryAdminListShowRequestDto, Integer page) {

        return inquiryRepository.adminInquirySearchWithBooleanBuilder(inquiryAdminListShowRequestDto, PageRequest.of(page, 10));
    }


    // 문의자, 트레이더 검색 조회
    // 정렬 순 셀렉트 박스 (최신순, 전략명)
    // 답변상태 셀렉트 박스 (전체, 답변 대기, 답변 완료)
    @Override
    public Page<Inquiry> findInquiries(InquiryListShowRequestDto inquiryListShowRequestDto, Integer page) {

        return inquiryRepository.inquirySearchWithBooleanBuilder(inquiryListShowRequestDto, PageRequest.of(page, 10));
    }

    @Override
    public InquiryAdminListOneShowResponseDto inquiryToInquiryAdminOneResponseDto(Inquiry inquiry) {

        String traderNickname = memberRepository.findById(inquiry.getTraderId()).orElseThrow(() -> new EntityNotFoundException("회원이 없습니다.")).getNickname();
        String methodIconPath = fileService.getFilePath(new FileRequest(FileReferenceType.METHOD, inquiry.getStrategy().getId()));
        StockListDto stockList = stockGetter.getStocks(inquiry.getStrategy().getId());

        return InquiryAdminListOneShowResponseDto.builder()
                .inquiryId(inquiry.getId())
                .traderId(inquiry.getTraderId())
                .traderNickname(traderNickname)
                .methodId(inquiry.getStrategy().getMethod().getId())
                .methodIconPath(methodIconPath)
                .cycle(inquiry.getStrategy().getCycle())
                .stockList(stockList)
                .strategyId(inquiry.getStrategy().getId())
                .strategyName(inquiry.getStrategy().getName())
                .statusCode(inquiry.getStrategy().getStatusCode())
                .inquiryRegistrationDate(inquiry.getInquiryRegistrationDate())
                .inquirerNickname(inquiry.getInquirer().getNickname())
                .inquiryStatus(inquiry.getInquiryStatus())
                .build();
    }

    @Override
    public InquiryAnswerAdminShowResponseDto inquiryIdToInquiryAnswerAdminShowResponseDto (
            Long inquiryId, Integer page, String closed, String searchType, String searchText) {

        Long userId = securityUtils.getUserIdInSecurityContext();
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new EntityNotFoundException("문의가 없습니다."));

        List<Inquiry> previousInquiryList = inquiryRepository.traderFindPreviousInquiry(inquiryId, userId, PageRequest.of(0, 1));

        String previousInquiryTitle;
        LocalDateTime previousInquiryWriteDate;
        if (previousInquiryList.isEmpty()) {
            previousInquiryTitle = null;
            previousInquiryWriteDate = null;
        } else {
            Inquiry previousInquiry = previousInquiryList.get(0);
            previousInquiryTitle = previousInquiry.getInquiryTitle();
            previousInquiryWriteDate = previousInquiry.getInquiryRegistrationDate();
        }

        List<Inquiry> nextInquiryList = inquiryRepository.traderFindNextInquiry(inquiryId, userId, PageRequest.of(0, 1));

        String nextInquiryTitle;
        LocalDateTime nextInquiryWriteDate;
        if (nextInquiryList.isEmpty()) {
            nextInquiryTitle = null;
            nextInquiryWriteDate = null;
        } else {
            Inquiry previousInquiry = nextInquiryList.get(0);
            nextInquiryTitle = previousInquiry.getInquiryTitle();
            nextInquiryWriteDate = previousInquiry.getInquiryRegistrationDate();
        }

        InquiryAnswer inquiryAnswer = inquiryAnswerRepository.findByInquiryId(inquiryId).orElseThrow(() -> new EntityNotFoundException("문의 답변이 없습니다."));
        Long inquiryAnswerId;
        String answerTitle;
        LocalDateTime answerRegistrationDate;
        String answerContent;
        if (inquiry.getInquiryStatus() == InquiryStatus.unclosed) {
            inquiryAnswerId = null;
            answerTitle = null;
            answerRegistrationDate = null;
            answerContent = null;
        } else {
            inquiryAnswerId = inquiryAnswer.getId();
            answerTitle = inquiryAnswer.getAnswerTitle();
            answerRegistrationDate = inquiryAnswer.getAnswerRegistrationDate();
            answerContent = inquiryAnswer.getAnswerContent();
        }

        String traderNickname = memberRepository.findById(inquiry.getTraderId()).orElseThrow(() -> new EntityNotFoundException("회원이 없습니다.")).getNickname();
        String traderProfileImagePath = fileService.getFilePath(new FileRequest(FileReferenceType.MEMBER, inquiry.getTraderId()));
        String methodIconPath = fileService.getFilePath(new FileRequest(FileReferenceType.METHOD, inquiry.getStrategy().getId()));
        StockListDto stockList = stockGetter.getStocks(inquiry.getStrategy().getId());

        return InquiryAnswerAdminShowResponseDto.builder()
                .page(page)
                .closed(closed)
                .searchType(searchType)
                .searchText(searchText)

                .inquiryId(inquiryId)
                .inquiryAnswerId(inquiryAnswerId)

                .inquiryTitle(inquiry.getInquiryTitle())
                .inquiryRegistrationDate(inquiry.getInquiryRegistrationDate())
                .inquirerNickname(inquiry.getInquirer().getNickname())
                .inquiryStatus(inquiry.getInquiryStatus())

                .methodId(inquiry.getStrategy().getMethod().getId())
                .methodIconPath(methodIconPath)
                .cycle(inquiry.getStrategy().getCycle())
                .stockList(stockList)
                .strategyId(inquiry.getStrategy().getId())
                .strategyName(inquiry.getStrategy().getName())
                .statusCode(inquiry.getStrategy().getStatusCode())

                .inquiryContent(inquiry.getInquiryContent())

                .traderId(inquiry.getTraderId())
                .traderNickname(traderNickname)
                .traderProfileImagePath(traderProfileImagePath)

                .answerTitle(answerTitle)
                .answerRegistrationDate(answerRegistrationDate)
                .answerContent(answerContent)

                .previousTitle(previousInquiryTitle)
                .previousWriteDate(previousInquiryWriteDate)
                .nextTitle(nextInquiryTitle)
                .nextWriteDate(nextInquiryWriteDate)
                .build();
    }

    @Override
    public InquirySavePageShowResponseDto strategyToInquirySavePageShowResponseDto(Strategy strategy) {

        String traderProfileImagePath = fileService.getFilePath(new FileRequest(FileReferenceType.MEMBER, strategy.getTrader().getId()));
        String methodIconPath = fileService.getFilePath(new FileRequest(FileReferenceType.METHOD, strategy.getId()));
        StockListDto stockList = stockGetter.getStocks(strategy.getId());

        return InquirySavePageShowResponseDto.builder()
                .methodId(strategy.getMethod().getId())
                .methodIconPath(methodIconPath)
                .cycle(strategy.getCycle())
                .stockList(stockList)
                .strategyId(strategy.getId())
                .strategyName(strategy.getName())
                .statusCode(strategy.getStatusCode())
                .traderId(strategy.getTrader().getId())
                .traderNickname(strategy.getTrader().getNickname())
                .traderProfileImagePath(traderProfileImagePath)
                .build();
    }

    @Override
    public InquiryListOneShowResponseDto inquiryToInquiryOneResponseDto(Inquiry inquiry) {

        String methodIconPath = fileService.getFilePath(new FileRequest(FileReferenceType.METHOD, inquiry.getStrategy().getId()));
        StockListDto stockList = stockGetter.getStocks(inquiry.getStrategy().getId());

        return InquiryListOneShowResponseDto.builder()
                .inquiryId(inquiry.getId())
                .inquiryTitle(inquiry.getInquiryTitle())

                .methodId(inquiry.getStrategy().getMethod().getId())
                .methodIconPath(methodIconPath)
                .cycle(inquiry.getStrategy().getCycle())
                .stockList(stockList)
                .strategyId(inquiry.getStrategy().getId())
                .strategyName(inquiry.getStrategy().getName())
                .statusCode(inquiry.getStrategy().getStatusCode())

                .inquiryRegistrationDate(inquiry.getInquiryRegistrationDate())
                .inquiryStatus(inquiry.getInquiryStatus())
                .build();
    }

    @Override
    public InquiryAnswerInquirerShowResponseDto inquiryIdToInquiryAnswerInquirerShowResponseDto(Long inquiryId, Integer page, String sort, String closed) {

        Long userId = securityUtils.getUserIdInSecurityContext();
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new EntityNotFoundException("문의가 없습니다."));

        if (!userId.equals(inquiry.getInquirer().getId())) {
            throw new MemberBadRequestException(MemberExceptionMessage.INVALID_MEMBER.getMessage());
        }

        List<Inquiry> previousInquiryList = inquiryRepository.inquirerFindPreviousInquiry(inquiryId, userId, PageRequest.of(0, 1));

        String previousInquiryTitle;
        LocalDateTime previousInquiryWriteDate;
        if (previousInquiryList.isEmpty()) {
            previousInquiryTitle = null;
            previousInquiryWriteDate = null;
        } else {
            Inquiry previousInquiry = previousInquiryList.get(0);
            previousInquiryTitle = previousInquiry.getInquiryTitle();
            previousInquiryWriteDate = previousInquiry.getInquiryRegistrationDate();
        }

        List<Inquiry> nextInquiryList = inquiryRepository.inquirerFindNextInquiry(inquiryId, userId, PageRequest.of(0, 1));

        String nextInquiryTitle;
        LocalDateTime nextInquiryWriteDate;
        if (nextInquiryList.isEmpty()) {
            nextInquiryTitle = null;
            nextInquiryWriteDate = null;
        } else {
            Inquiry previousInquiry = nextInquiryList.get(0);
            nextInquiryTitle = previousInquiry.getInquiryTitle();
            nextInquiryWriteDate = previousInquiry.getInquiryRegistrationDate();
        }

        Long inquiryAnswerId;
        String answerTitle;
        LocalDateTime answerRegistrationDate;
        String answerContent;
        if (inquiry.getInquiryStatus() == InquiryStatus.unclosed) {
            inquiryAnswerId = null;
            answerTitle = null;
            answerRegistrationDate = null;
            answerContent = null;
        } else {
            InquiryAnswer inquiryAnswer = inquiryAnswerRepository.findByInquiryId(inquiryId).orElseThrow(() -> new EntityNotFoundException("문의 답변이 없습니다."));
            inquiryAnswerId = inquiryAnswer.getId();
            answerTitle = inquiryAnswer.getAnswerTitle();
            answerRegistrationDate = inquiryAnswer.getAnswerRegistrationDate();
            answerContent = inquiryAnswer.getAnswerContent();
        }

        String traderNickname = memberRepository.findById(inquiry.getTraderId()).orElseThrow(() -> new EntityNotFoundException("회원이 없습니다.")).getNickname();
        String traderProfileImagePath = fileService.getFilePath(new FileRequest(FileReferenceType.MEMBER, inquiry.getTraderId()));
        String methodIconPath = fileService.getFilePath(new FileRequest(FileReferenceType.METHOD, inquiry.getStrategy().getId()));
        StockListDto stockList = stockGetter.getStocks(inquiry.getStrategy().getId());

        return InquiryAnswerInquirerShowResponseDto.builder()
                .page(page)
                .sort(sort)
                .closed(closed)

                .inquiryId(inquiryId)
                .inquiryAnswerId(inquiryAnswerId)

                .inquiryTitle(inquiry.getInquiryTitle())
                .inquiryRegistrationDate(inquiry.getInquiryRegistrationDate())
                .inquiryStatus(inquiry.getInquiryStatus())

                .methodId(inquiry.getStrategy().getMethod().getId())
                .methodIconPath(methodIconPath)
                .cycle(inquiry.getStrategy().getCycle())
                .stockList(stockList)
                .strategyId(inquiry.getStrategy().getId())
                .strategyName(inquiry.getStrategy().getName())
                .statusCode(inquiry.getStrategy().getStatusCode())

                .traderId(inquiry.getTraderId())
                .traderNickname(traderNickname)
                .traderProfileImagePath(traderProfileImagePath)

                .inquiryContent(inquiry.getInquiryContent())

                .answerTitle(answerTitle)
                .answerRegistrationDate(answerRegistrationDate)
                .answerContent(answerContent)

                .previousTitle(previousInquiryTitle)
                .previousWriteDate(previousInquiryWriteDate)
                .nextTitle(nextInquiryTitle)
                .nextWriteDate(nextInquiryWriteDate)
                .build();
    }

    @Override
    public InquiryAnswerTraderShowResponseDto inquiryIdToInquiryAnswerTraderShowResponseDto(Long inquiryId, Integer page, String sort, String closed) {

        Long userId = securityUtils.getUserIdInSecurityContext();
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new EntityNotFoundException("문의가 없습니다."));

        if (!userId.equals(inquiry.getInquirer().getId())) {
            throw new MemberBadRequestException(MemberExceptionMessage.INVALID_MEMBER.getMessage());
        }

        List<Inquiry> previousInquiryList = inquiryRepository.traderFindPreviousInquiry(inquiryId, userId, PageRequest.of(0, 1));

        String previousInquiryTitle;
        LocalDateTime previousInquiryWriteDate;
        if (previousInquiryList.isEmpty()) {
            previousInquiryTitle = null;
            previousInquiryWriteDate = null;
        } else {
            Inquiry previousInquiry = previousInquiryList.get(0);
            previousInquiryTitle = previousInquiry.getInquiryTitle();
            previousInquiryWriteDate = previousInquiry.getInquiryRegistrationDate();
        }

        List<Inquiry> nextInquiryList = inquiryRepository.traderFindNextInquiry(inquiryId, userId, PageRequest.of(0, 1));

        String nextInquiryTitle;
        LocalDateTime nextInquiryWriteDate;
        if (nextInquiryList.isEmpty()) {
            nextInquiryTitle = null;
            nextInquiryWriteDate = null;
        } else {
            Inquiry previousInquiry = nextInquiryList.get(0);
            nextInquiryTitle = previousInquiry.getInquiryTitle();
            nextInquiryWriteDate = previousInquiry.getInquiryRegistrationDate();
        }

        Long inquiryAnswerId;
        String answerTitle;
        LocalDateTime answerRegistrationDate;
        String answerContent;
        if (inquiry.getInquiryStatus() == InquiryStatus.unclosed) {
            inquiryAnswerId = null;
            answerTitle = null;
            answerRegistrationDate = null;
            answerContent = null;
        } else {
            InquiryAnswer inquiryAnswer = inquiryAnswerRepository.findByInquiryId(inquiryId).orElseThrow(() -> new EntityNotFoundException("문의 답변이 없습니다."));
            inquiryAnswerId = inquiryAnswer.getId();
            answerTitle = inquiryAnswer.getAnswerTitle();
            answerRegistrationDate = inquiryAnswer.getAnswerRegistrationDate();
            answerContent = inquiryAnswer.getAnswerContent();
        }

        String traderNickname = memberRepository.findById(inquiry.getTraderId()).orElseThrow(() -> new EntityNotFoundException("회원이 없습니다.")).getNickname();
        String traderProfileImagePath = fileService.getFilePath(new FileRequest(FileReferenceType.MEMBER, inquiry.getTraderId()));
        String methodIconPath = fileService.getFilePath(new FileRequest(FileReferenceType.METHOD, inquiry.getStrategy().getId()));
        StockListDto stockList = stockGetter.getStocks(inquiry.getStrategy().getId());

        return InquiryAnswerTraderShowResponseDto.builder()
                .page(page)
                .sort(sort)
                .closed(closed)

                .inquiryId(inquiryId)
                .inquiryAnswerId(inquiryAnswerId)

                .inquiryTitle(inquiry.getInquiryTitle())
                .inquiryRegistrationDate(inquiry.getInquiryRegistrationDate())
                .inquirerNickname(inquiry.getInquirer().getNickname())
                .inquiryStatus(inquiry.getInquiryStatus())

                .methodId(inquiry.getStrategy().getMethod().getId())
                .methodIconPath(methodIconPath)
                .cycle(inquiry.getStrategy().getCycle())
                .stockList(stockList)
                .strategyId(inquiry.getStrategy().getId())
                .strategyName(inquiry.getStrategy().getName())
                .statusCode(inquiry.getStrategy().getStatusCode())

                .traderId(inquiry.getTraderId())
                .traderNickname(traderNickname)
                .traderProfileImagePath(traderProfileImagePath)

                .inquiryContent(inquiry.getInquiryContent())

                .answerTitle(answerTitle)
                .answerRegistrationDate(answerRegistrationDate)
                .answerContent(answerContent)

                .previousTitle(previousInquiryTitle)
                .previousWriteDate(previousInquiryWriteDate)
                .nextTitle(nextInquiryTitle)
                .nextWriteDate(nextInquiryWriteDate)
                .build();
    }
}
