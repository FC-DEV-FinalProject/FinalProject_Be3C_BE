package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.*;
import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryAnswer;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.service.InquiryAnswerService;
import com.be3c.sysmetic.domain.member.service.InquiryService;
import com.be3c.sysmetic.domain.strategy.service.MemberService;
import com.be3c.sysmetic.domain.strategy.service.MemberServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.StrategyService;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.PageResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class InquiryController {

    private final InquiryService inquiryService;
    private final InquiryAnswerService inquiryAnswerService;
    private final MemberService memberService;
    private final StrategyService strategyService;

    private final Integer pageSize = 10; // 한 페이지 크기
    private final MemberServiceImpl memberServiceImpl;

    // 관리자 문의 조회 API
//    @GetMapping("/admin/inquiry")
//    public ResponseEntity<ApiResponse<PageResponse<InquiryDto>>> showAdminInquiry(
//            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
//            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed) {
//        long totalCountInquiry = inquiryService.totalCountAll(); // 전체 데이터 수
//        int totalPageCount; // 전체 페이지 수
//        if (totalCountInquiry % pageSize == 0) {
//            totalPageCount = (int) (totalCountInquiry / pageSize);
//        } else {
//            totalPageCount = (int) (totalCountInquiry / pageSize) + 1;
//        }
//        int pageStart = (page - 1) * pageSize; // 페이지 시작 위치
////        List<Inquiry> inquiryList = inquiryService.findInquiryAll(pageStart, pageSize);
//        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);
//        List<Inquiry> inquiryList = inquiryService.findInquiryByStatus(inquiryStatus, pageStart, pageSize);
//
//        List<InquiryDto> collect = inquiryList.stream()
//                .map(i -> new InquiryDto(i.getId(),
//                                        i.getInquiryAnswer().getId(),
//                                        i.getStrategy().getId(),
//                                        i.getStrategy().getTrader().getId(),
//                                        i.getMember().getId(),
//                                        i.getInquiryStatus(),
//                                        i.getInquiryTitle(),
//                                        i.getInquiryContent(),
//                                        i.getInquiryRegistrationDate()))
//                .collect(Collectors.toList());
//
//        PageResponse<InquiryDto> adminInquiryPage = PageResponse.<InquiryDto>builder()
//                                            .currentPage(page) // 현재 페이지
//                                            .pageSize(pageSize) // 한 페이지 크기
//                                            .totalElement(totalCountInquiry) // 전체 데이터 수
//                                            .totalPages(totalPageCount) // 전체 페이지 수
//                                            .content(collect)
//                                            .build();
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(ApiResponse.success(adminInquiryPage));
//    }

    // 관리자 문의 검색 API : searchAdminInquiry
    @GetMapping("/admin/inquiry")
    public ResponseEntity<ApiResponse<PageResponse<InquiryDto>>> showAdminInquiry(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "search_condition", required = false) String searchCondition,
            @RequestParam(value = "search_keyword", required = false) String searchKeyword) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        ShowInquiryRequestDto showInquiryRequestDto = new ShowInquiryRequestDto();
        showInquiryRequestDto.setTab(inquiryStatus);
        showInquiryRequestDto.setSearchCondition(searchCondition);
        showInquiryRequestDto.setSearchKeyword(searchKeyword);

        long totalCountInquiry = inquiryService.totalCountByStrategyQuestionerTrader(showInquiryRequestDto); // 전체 데이터 수
        int totalPageCount; // 전체 페이지 수
        if (totalCountInquiry % pageSize == 0) {
            totalPageCount = (int) (totalCountInquiry / pageSize);
        } else {
            totalPageCount = (int) (totalCountInquiry / pageSize) + 1;
        }
        int pageStart = (page - 1) * pageSize; // 페이지 시작 위치
//        List<Inquiry> inquiryList = inquiryService.findInquiryAll(pageStart, pageSize);
        List<Inquiry> inquiryList = inquiryService.findInquiresByStrategyQuestionerTrader(showInquiryRequestDto, pageStart, pageSize);

        List<InquiryDto> collect = inquiryList.stream()
                .map(i -> new InquiryDto(i.getId(),
                        i.getInquiryAnswer().getId(),
                        i.getStrategy().getId(),
                        i.getStrategy().getTrader().getId(),
                        i.getMember().getId(),
                        i.getInquiryContent(),

                        i.getStrategy().getName(),
                        i.getInquiryTitle(),
                        i.getStrategy().getTrader().getName(),
                        i.getInquiryRegistrationDate(),
                        i.getInquiryStatus()))
                .collect(Collectors.toList());

        PageResponse<InquiryDto> adminInquiryPage = PageResponse.<InquiryDto>builder()
                .currentPage(page) // 현재 페이지
                .pageSize(pageSize) // 한 페이지 크기
                .totalElement(totalCountInquiry) // 전체 데이터 수
                .totalPages(totalPageCount) // 전체 페이지 수
                .content(collect)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(adminInquiryPage));
    }

    // 관리자 문의 상세 조회 API
    @GetMapping("/admin/inquiry/view")
    public ResponseEntity<ApiResponse<InquiryAnswerDto>> showAdminInquiryDetail(
            @RequestParam(value = "no") long no,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "search_condition", required = false) String searchCondition,
            @RequestParam(value = "search_keyword", required = false) String searchKeyword) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        List<InquiryAnswer> inquiryAnswerList = inquiryAnswerService.findThatInquiryAnswers(no);
        InquiryAnswer inquiryAnswer = inquiryAnswerList.get(0);

        InquiryAnswerDto inquiryAnswerDto = new InquiryAnswerDto();
        inquiryAnswerDto.setId(inquiryAnswer.getId());
        inquiryAnswerDto.setInquiryId(inquiryAnswer.getId());
        inquiryAnswerDto.setStrategyName(inquiryAnswer.getInquiry().getStrategy().getName());
        inquiryAnswerDto.setInquiryTitle(inquiryAnswer.getInquiry().getInquiryTitle());
        inquiryAnswerDto.setInquiryContent(inquiryAnswer.getInquiry().getInquiryContent());
        inquiryAnswerDto.setMemberName(inquiryAnswer.getInquiry().getMember().getName());
        inquiryAnswerDto.setInquiryRegistrationDate(inquiryAnswer.getInquiry().getInquiryRegistrationDate());
        inquiryAnswerDto.setAnswerContent(inquiryAnswer.getAnswerContent());
        inquiryAnswerDto.setTraderName(inquiryAnswer.getInquiry().getStrategy().getTrader().getName());
        inquiryAnswerDto.setAnswerRegistrationDate(inquiryAnswer.getAnswerRegistrationDate());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(inquiryAnswerDto));
    }

    // 질문자 문의 조회 API : showMemberInquiry
//    @GetMapping("/member/inquiry")
//    public ResponseEntity<ApiResponse<PageResponse<InquiryDto>>> showMemberInquiry(
//            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
//            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
//            @RequestParam(value = "search_condition", required = false) String searchCondition,
//            @RequestParam(value = "search_keyword", required = false) String searchKeyword) {
//        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);
//
//        ShowInquiryRequestDto showInquiryRequestDto = new ShowInquiryRequestDto();
//        showInquiryRequestDto.setMemberId(123L);
//        showInquiryRequestDto.setTab(inquiryStatus);
//        showInquiryRequestDto.setSearchCondition(searchCondition);
//        showInquiryRequestDto.setSearchKeyword(searchKeyword);
//
//        long totalCountInquiry = inquiryService.totalCountByStrategyQuestionerTrader(showInquiryRequestDto); // 전체 데이터 수
//        int totalPageCount; // 전체 페이지 수
//        if (totalCountInquiry % pageSize == 0) {
//            totalPageCount = (int) (totalCountInquiry / pageSize);
//        } else {
//            totalPageCount = (int) (totalCountInquiry / pageSize) + 1;
//        }
//        int pageStart = (page - 1) * pageSize; // 페이지 시작 위치
//
//        List<Inquiry> inquiryList = inquiryService.findInquiresByStrategyQuestionerTrader(showInquiryRequestDto, pageStart, pageSize);
//
//        List<InquiryDto> collect = inquiryList.stream()
//                .map(i -> new InquiryDto(i.getId(),
//                        i.getInquiryAnswer().getId(),
//                        i.getStrategy().getId(),
//                        i.getStrategy().getTrader().getId(),
//                        i.getMember().getId(),
//                        i.getInquiryContent(),
//
//                        i.getStrategy().getName(),
//                        i.getInquiryTitle(),
//                        i.getStrategy().getTrader().getName(),
//                        i.getInquiryRegistrationDate(),
//                        i.getInquiryStatus()))
//                .collect(Collectors.toList());
//
//        PageResponse<InquiryDto> memberInquiryPage = PageResponse.<InquiryDto>builder()
//                .currentPage(page) // 현재 페이지
//                .pageSize(pageSize) // 한 페이지 크기
//                .totalElement(totalCountInquiry) // 전체 데이터 수
//                .totalPages(totalPageCount) // 전체 페이지 수
//                .content(collect)
//                .build();
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(ApiResponse.success(memberInquiryPage));
//    }

    // 질문자 문의 검색 API : searchMemberInquiry
    @GetMapping("/member/inquiry")
    public ResponseEntity<ApiResponse<PageResponse<InquiryDto>>> searchMemberInquiry(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "search_condition", required = false) String searchCondition,
            @RequestParam(value = "search_keyword", required = false) String searchKeyword) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        ShowInquiryRequestDto showInquiryRequestDto = new ShowInquiryRequestDto();
        showInquiryRequestDto.setMemberId(123L);
        showInquiryRequestDto.setTab(inquiryStatus);
        showInquiryRequestDto.setSearchCondition(searchCondition);
        showInquiryRequestDto.setSearchKeyword(searchKeyword);

        long totalCountInquiry = inquiryService.totalCountByStrategyQuestionerTrader(showInquiryRequestDto); // 전체 데이터 수
        int totalPageCount; // 전체 페이지 수
        if (totalCountInquiry % pageSize == 0) {
            totalPageCount = (int) (totalCountInquiry / pageSize);
        } else {
            totalPageCount = (int) (totalCountInquiry / pageSize) + 1;
        }
        int pageStart = (page - 1) * pageSize; // 페이지 시작 위치

        List<Inquiry> inquiryList = inquiryService.findInquiresByStrategyQuestionerTrader(showInquiryRequestDto, pageStart, pageSize);

        List<InquiryDto> collect = inquiryList.stream()
                .map(i -> new InquiryDto(i.getId(),
                        i.getInquiryAnswer().getId(),
                        i.getStrategy().getId(),
                        i.getStrategy().getTrader().getId(),
                        i.getMember().getId(),
                        i.getInquiryContent(),

                        i.getStrategy().getName(),
                        i.getInquiryTitle(),
                        i.getStrategy().getTrader().getName(),
                        i.getInquiryRegistrationDate(),
                        i.getInquiryStatus()))
                .collect(Collectors.toList());

        PageResponse<InquiryDto> memberInquiryPage = PageResponse.<InquiryDto>builder()
                .currentPage(page) // 현재 페이지
                .pageSize(pageSize) // 한 페이지 크기
                .totalElement(totalCountInquiry) // 전체 데이터 수
                .totalPages(totalPageCount) // 전체 페이지 수
                .content(collect)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(memberInquiryPage));
    }

    // 질문자 문의 상세 조회 API : showMemberInquiryDetail
    @GetMapping("/member/inquiry/view")
    public ResponseEntity<ApiResponse<InquiryAnswerDto>> showMemberInquiryDetail(
            @RequestParam(value = "no") long no,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "search_condition", required = false) String searchCondition,
            @RequestParam(value = "search_keyword", required = false) String searchKeyword) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        List<InquiryAnswer> inquiryAnswerList = inquiryAnswerService.findThatInquiryAnswers(no);
        InquiryAnswer inquiryAnswer = inquiryAnswerList.get(0);

        InquiryAnswerDto inquiryAnswerDto = new InquiryAnswerDto();
        inquiryAnswerDto.setId(inquiryAnswer.getId());
        inquiryAnswerDto.setInquiryId(inquiryAnswer.getId());
        inquiryAnswerDto.setStrategyName(inquiryAnswer.getInquiry().getStrategy().getName());
        inquiryAnswerDto.setInquiryTitle(inquiryAnswer.getInquiry().getInquiryTitle());
        inquiryAnswerDto.setInquiryContent(inquiryAnswer.getInquiry().getInquiryContent());
        inquiryAnswerDto.setMemberName(inquiryAnswer.getInquiry().getMember().getName());
        inquiryAnswerDto.setInquiryRegistrationDate(inquiryAnswer.getInquiry().getInquiryRegistrationDate());
        inquiryAnswerDto.setAnswerContent(inquiryAnswer.getAnswerContent());
        inquiryAnswerDto.setTraderName(inquiryAnswer.getInquiry().getStrategy().getTrader().getName());
        inquiryAnswerDto.setAnswerRegistrationDate(inquiryAnswer.getAnswerRegistrationDate());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(inquiryAnswerDto));
    }

    // 질문자 문의 등록 API
    @PostMapping("/member/inquiry")
    public ResponseEntity<ApiResponse<Long>> saveMemberInquiry(
            @RequestBody SaveInquiryRequestDto saveInquiryRequestDto) {

        Long inquiryId = inquiryService.registerInquiry(saveInquiryRequestDto.getMemberId(),
                saveInquiryRequestDto.getStrategyId(),
                saveInquiryRequestDto.getInquiryTitle(),
                saveInquiryRequestDto.getInquiryContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(inquiryId));
    }

    // 질문자 문의 수정 API
    @PutMapping("/member/inquiry")
    public ResponseEntity<ApiResponse<Long>> modifyMemberInquiry(
            @RequestBody @Valid ModifyInquiryRequestDto modifyInquiryRequestDto) {

        inquiryService.modifyInquiry(modifyInquiryRequestDto.getInquiryId(),
                modifyInquiryRequestDto.getInquiryTitle(),
                modifyInquiryRequestDto.getInquiryContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(modifyInquiryRequestDto.getInquiryId()));
    }

    // 질문자 문의 삭제 API
    @DeleteMapping("/member/inquiry")
    public ResponseEntity<ApiResponse<Long>> deleteMemberInquiry(
            @RequestBody @Valid DeleteInquiryRequestDto deleteInquiryRequestDto) {

        inquiryService.deleteInquiry(deleteInquiryRequestDto.getInquiryId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(deleteInquiryRequestDto.getInquiryId()));
    }

    @Data
    @AllArgsConstructor
    static class DeleteInquiryRequestDto {
        private Long inquiryId;
    }

    // 트레이더 문의 답변 등록 API
    @PostMapping("/trader/inquiry")
    public ResponseEntity<ApiResponse<Long>> saveTraderInquiry(
            @RequestBody @Valid SaveInquiryDetailRequestDto saveInquiryDetailRequestDto) {

        Long inquiryAnswerId = inquiryAnswerService.registerInquiryAnswer(saveInquiryDetailRequestDto.getInquiryId(),
                saveInquiryDetailRequestDto.getAnswerContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(inquiryAnswerId));
    }

    // 트레이더 문의 조회 API
//    @GetMapping("/trader/inquiry")
//    public ResponseEntity<ApiResponse<PageResponse<InquiryDto>>> showTraderInquiry(
//            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
//            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
//            @RequestParam(value = "search_ci", required = false) String searchCondition,
//            @RequestParam(value = "search_keyword", required = false) String searchKeyword) {
//        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);
//
//        ShowInquiryRequestDto showInquiryRequestDto = new ShowInquiryRequestDto();
//        showInquiryRequestDto.setTraderId(123L);
//        showInquiryRequestDto.setTab(inquiryStatus);
//        showInquiryRequestDto.setSearchCondition(searchCondition);
//        showInquiryRequestDto.setSearchKeyword(searchKeyword);
//
//        long totalCountInquiry = inquiryService.totalCountByStrategyQuestionerTrader(showInquiryRequestDto); // 전체 데이터 수
//        int totalPageCount; // 전체 페이지 수
//        if (totalCountInquiry % pageSize == 0) {
//            totalPageCount = (int) (totalCountInquiry / pageSize);
//        } else {
//            totalPageCount = (int) (totalCountInquiry / pageSize) + 1;
//        }
//        int pageStart = (page - 1) * pageSize; // 페이지 시작 위치
//
//        List<Inquiry> inquiryList = inquiryService.findInquiresByStrategyQuestionerTrader(showInquiryRequestDto, pageStart, pageSize);
//
//        List<InquiryDto> collect = inquiryList.stream()
//                .map(i -> new InquiryDto(i.getId(),
//                        i.getInquiryAnswer().getId(),
//                        i.getStrategy().getId(),
//                        i.getStrategy().getTrader().getId(),
//                        i.getMember().getId(),
//                        i.getInquiryContent(),
//
//                        i.getStrategy().getName(),
//                        i.getInquiryTitle(),
//                        i.getStrategy().getTrader().getName(),
//                        i.getInquiryRegistrationDate(),
//                        i.getInquiryStatus()))
//                .collect(Collectors.toList());
//
//        PageResponse<InquiryDto> traderInquiryPage = PageResponse.<InquiryDto>builder()
//                .currentPage(page) // 현재 페이지
//                .pageSize(pageSize) // 한 페이지 크기
//                .totalElement(totalCountInquiry) // 전체 데이터 수
//                .totalPages(totalPageCount) // 전체 페이지 수
//                .content(collect)
//                .build();
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(ApiResponse.success(traderInquiryPage));
//    }

    // 트레이더 문의 검색 API
    @GetMapping("/trader/inquiry")
    public ResponseEntity<ApiResponse<PageResponse<InquiryDto>>> searchTraderInquiry(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "search_condition", required = false) String searchCondition,
            @RequestParam(value = "search_keyword", required = false) String searchKeyword) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        ShowInquiryRequestDto showInquiryRequestDto = new ShowInquiryRequestDto();
        showInquiryRequestDto.setTraderId(1L);
        showInquiryRequestDto.setTab(inquiryStatus);
        showInquiryRequestDto.setSearchCondition(searchCondition);
        showInquiryRequestDto.setSearchKeyword(searchKeyword);

        long totalCountInquiry = inquiryService.totalCountByStrategyQuestionerTrader(showInquiryRequestDto); // 전체 데이터 수
        int totalPageCount; // 전체 페이지 수
        if (totalCountInquiry % pageSize == 0) {
            totalPageCount = (int) (totalCountInquiry / pageSize);
        } else {
            totalPageCount = (int) (totalCountInquiry / pageSize) + 1;
        }
        int pageStart = (page - 1) * pageSize; // 페이지 시작 위치

        List<Inquiry> inquiryList = inquiryService.findInquiresByStrategyQuestionerTrader(showInquiryRequestDto, pageStart, pageSize);

        List<InquiryDto> collect = inquiryList.stream()
                .map(i -> new InquiryDto(i.getId(),
                        i.getInquiryAnswer().getId(),
                        i.getStrategy().getId(),
                        i.getStrategy().getTrader().getId(),
                        i.getMember().getId(),
                        i.getInquiryContent(),

                        i.getStrategy().getName(),
                        i.getInquiryTitle(),
                        i.getStrategy().getTrader().getName(),
                        i.getInquiryRegistrationDate(),
                        i.getInquiryStatus()))
                .collect(Collectors.toList());

        PageResponse<InquiryDto> traderInquiryPage = PageResponse.<InquiryDto>builder()
                .currentPage(page) // 현재 페이지
                .pageSize(pageSize) // 한 페이지 크기
                .totalElement(totalCountInquiry) // 전체 데이터 수
                .totalPages(totalPageCount) // 전체 페이지 수
                .content(collect)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(traderInquiryPage));
    }

    // 트레이더 문의 상세 조회 API
    @GetMapping("/trader/inquiry/view")
    public ResponseEntity<ApiResponse<InquiryAnswerDto>> showTraderInquiryDetail(
            @RequestParam(value = "no") long no,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "search_condition", required = false) String searchCondition,
            @RequestParam(value = "search_keyword", required = false) String searchKeyword) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        List<InquiryAnswer> inquiryAnswerList = inquiryAnswerService.findThatInquiryAnswers(no);
        InquiryAnswer inquiryAnswer = inquiryAnswerList.get(0);

        InquiryAnswerDto inquiryAnswerDto = new InquiryAnswerDto();
        inquiryAnswerDto.setId(inquiryAnswer.getId());
        inquiryAnswerDto.setInquiryId(inquiryAnswer.getId());
        inquiryAnswerDto.setStrategyName(inquiryAnswer.getInquiry().getStrategy().getName());
        inquiryAnswerDto.setInquiryTitle(inquiryAnswer.getInquiry().getInquiryTitle());
        inquiryAnswerDto.setInquiryContent(inquiryAnswer.getInquiry().getInquiryContent());
        inquiryAnswerDto.setMemberName(inquiryAnswer.getInquiry().getMember().getName());
        inquiryAnswerDto.setInquiryRegistrationDate(inquiryAnswer.getInquiry().getInquiryRegistrationDate());
        inquiryAnswerDto.setAnswerContent(inquiryAnswer.getAnswerContent());
        inquiryAnswerDto.setTraderName(inquiryAnswer.getInquiry().getStrategy().getTrader().getName());
        inquiryAnswerDto.setAnswerRegistrationDate(inquiryAnswer.getAnswerRegistrationDate());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(inquiryAnswerDto));
    }
}
