package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.inquiry.*;
import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryAnswer;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.service.InquiryAnswerService;
import com.be3c.sysmetic.domain.member.service.InquiryService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "문의 API", description = "관리자, 트레이더, 투자자 문의 API")
@RequiredArgsConstructor
@RestController
public class InquiryController {

    private final InquiryService inquiryService;
    private final InquiryAnswerService inquiryAnswerService;

    private final Integer pageSize = 10; // 한 페이지 크기

    /*
        관리자 문의 조회, 검색 API
        1. 문의 데이터 조회에 성공했을 때 : OK
        2. 페이지 내에 한 개의 문의도 존재하지 않을 때 : NOT_FOUND
     */
    @GetMapping("/admin/inquiry")
    public ResponseEntity<APIResponse<PageResponse<InquiryAdminShowResponseDto>>> showAdminInquiry (
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "searhType", required = false) String searhType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        InquiryShowRequestDto inquiryShowRequestDto = new InquiryShowRequestDto();
        inquiryShowRequestDto.setTab(inquiryStatus);
        inquiryShowRequestDto.setSearchCondition(searhType);
        inquiryShowRequestDto.setSearchKeyword(searchText);

        long totalCountInquiry = inquiryService.totalCountByStrategyQuestionerTrader(inquiryShowRequestDto); // 전체 데이터 수
        int totalPageCount; // 전체 페이지 수
        if (totalCountInquiry % pageSize == 0) {
            totalPageCount = (int) (totalCountInquiry / pageSize);
        } else {
            totalPageCount = (int) (totalCountInquiry / pageSize) + 1;
        }
        int pageStart = (page - 1) * pageSize; // 페이지 시작 위치

        List<Inquiry> inquiryList = inquiryService.findInquiresByStrategyQuestionerTrader(inquiryShowRequestDto, pageStart, pageSize);

        List<InquiryAdminShowResponseDto> collect = inquiryList.stream()
                .map(i -> new InquiryAdminShowResponseDto(i.getId(),
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

        PageResponse<InquiryAdminShowResponseDto> adminInquiryPage = PageResponse.<InquiryAdminShowResponseDto>builder()
                .currentPage(page) // 현재 페이지
                .pageSize(pageSize) // 한 페이지 크기
                .totalElement(totalCountInquiry) // 전체 데이터 수
                .totalPages(totalPageCount) // 전체 페이지 수
                .content(collect)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(adminInquiryPage));
    }


    // 관리자 문의 상세 조회 API
    @GetMapping("/admin/inquiry/{inquiryId}/view")
    public ResponseEntity<APIResponse<InquiryAnswerDto>> showAdminInquiryDetail (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "searhType", required = false) String searhType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        List<InquiryAnswer> inquiryAnswerList = inquiryAnswerService.findThatInquiryAnswers(inquiryId);
        InquiryAnswer inquiryAnswer = inquiryAnswerList.get(0);

        InquiryAnswerDto inquiryAnswerDto = new InquiryAnswerDto();
        inquiryAnswerDto.setId(inquiryAnswer.getId());
        inquiryAnswerDto.setInquiryId(inquiryAnswer.getInquiry().getId());
        inquiryAnswerDto.setStrategyName(inquiryAnswer.getInquiry().getStrategy().getName());
        inquiryAnswerDto.setInquiryTitle(inquiryAnswer.getInquiry().getInquiryTitle());
        inquiryAnswerDto.setInquiryContent(inquiryAnswer.getInquiry().getInquiryContent());
        inquiryAnswerDto.setMemberName(inquiryAnswer.getInquiry().getMember().getName());
        inquiryAnswerDto.setInquiryRegistrationDate(inquiryAnswer.getInquiry().getInquiryRegistrationDate());
        inquiryAnswerDto.setAnswerContent(inquiryAnswer.getAnswerContent());
        inquiryAnswerDto.setTraderName(inquiryAnswer.getInquiry().getStrategy().getTrader().getName());
        inquiryAnswerDto.setAnswerRegistrationDate(inquiryAnswer.getAnswerRegistrationDate());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryAnswerDto));
    }


    // 질문자 문의 등록 API
    @PostMapping("/strategy/inquiry")
    public ResponseEntity<APIResponse<Long>> saveMemberInquiry(
            @RequestBody SaveInquiryRequestDto saveInquiryRequestDto) {

        Long inquiryId = inquiryService.registerInquiry(saveInquiryRequestDto.getMemberId(),
                saveInquiryRequestDto.getStrategyId(),
                saveInquiryRequestDto.getInquiryTitle(),
                saveInquiryRequestDto.getInquiryContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryId));
    }


    // 질문자 문의 조회, 검색 API
    @GetMapping("/member/inquiry")
    public ResponseEntity<APIResponse<PageResponse<InquiryAdminShowResponseDto>>> showMemberInquiry (
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "searhType", required = false) String searhType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        InquiryShowRequestDto inquiryShowRequestDto = new InquiryShowRequestDto();
        inquiryShowRequestDto.setMemberId(123L); // 현재 로그인한 회원의 아이디
        inquiryShowRequestDto.setTab(inquiryStatus);
        inquiryShowRequestDto.setSearchCondition(searhType);
        inquiryShowRequestDto.setSearchKeyword(searchText);

        long totalCountInquiry = inquiryService.totalCountByStrategyQuestionerTrader(inquiryShowRequestDto); // 전체 데이터 수
        int totalPageCount; // 전체 페이지 수
        if (totalCountInquiry % pageSize == 0) {
            totalPageCount = (int) (totalCountInquiry / pageSize);
        } else {
            totalPageCount = (int) (totalCountInquiry / pageSize) + 1;
        }
        int pageStart = (page - 1) * pageSize; // 페이지 시작 위치

        List<Inquiry> inquiryList = inquiryService.findInquiresByStrategyQuestionerTrader(inquiryShowRequestDto, pageStart, pageSize);

        List<InquiryAdminShowResponseDto> collect = inquiryList.stream()
                .map(i -> new InquiryAdminShowResponseDto(i.getId(),
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

        PageResponse<InquiryAdminShowResponseDto> memberInquiryPage = PageResponse.<InquiryAdminShowResponseDto>builder()
                .currentPage(page) // 현재 페이지
                .pageSize(pageSize) // 한 페이지 크기
                .totalElement(totalCountInquiry) // 전체 데이터 수
                .totalPages(totalPageCount) // 전체 페이지 수
                .content(collect)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(memberInquiryPage));
    }


    // 질문자 문의 상세 조회 API
    @GetMapping("/member/inquiry/{inquiryId}/view")
    public ResponseEntity<APIResponse<InquiryAnswerDto>> showMemberInquiryDetail (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "searhType", required = false) String searhType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        List<InquiryAnswer> inquiryAnswerList = inquiryAnswerService.findThatInquiryAnswers(inquiryId);
        InquiryAnswer inquiryAnswer = inquiryAnswerList.get(0);

        InquiryAnswerDto inquiryAnswerDto = new InquiryAnswerDto();
        inquiryAnswerDto.setId(inquiryAnswer.getId());
        inquiryAnswerDto.setInquiryId(inquiryAnswer.getInquiry().getId());
        inquiryAnswerDto.setStrategyName(inquiryAnswer.getInquiry().getStrategy().getName());
        inquiryAnswerDto.setInquiryTitle(inquiryAnswer.getInquiry().getInquiryTitle());
        inquiryAnswerDto.setInquiryContent(inquiryAnswer.getInquiry().getInquiryContent());
        inquiryAnswerDto.setMemberName(inquiryAnswer.getInquiry().getMember().getName());
        inquiryAnswerDto.setInquiryRegistrationDate(inquiryAnswer.getInquiry().getInquiryRegistrationDate());
        inquiryAnswerDto.setAnswerContent(inquiryAnswer.getAnswerContent());
        inquiryAnswerDto.setTraderName(inquiryAnswer.getInquiry().getStrategy().getTrader().getName());
        inquiryAnswerDto.setAnswerRegistrationDate(inquiryAnswer.getAnswerRegistrationDate());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryAnswerDto));
    }

    // 질문자 문의 수정 화면 조회 API
    @GetMapping("/member/inquiry/{inquiryId}/modify")
    public ResponseEntity<APIResponse<InquiryAnswerDto>> showModifyMemberInquiry (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "search_condition", required = false) String searchCondition,
            @RequestParam(value = "search_keyword", required = false) String searchKeyword) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);


        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryAnswerDto));
    }

    // 질문자 문의 수정 API
    @PutMapping("/member/inquiry/{inquiryId}/modify")
    public ResponseEntity<APIResponse<Long>> modifyMemberInquiry (
            @PathVariable Long inquiryId,
            @RequestBody @Valid ModifyInquiryRequestDto modifyInquiryRequestDto) {

        inquiryService.modifyInquiry(modifyInquiryRequestDto.getInquiryId(),
                modifyInquiryRequestDto.getInquiryTitle(),
                modifyInquiryRequestDto.getInquiryContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(modifyInquiryRequestDto.getInquiryId()));
    }


    // 질문자 문의 삭제 API
    @DeleteMapping("/member/inquiry/{inquiryId}/delete")
    public ResponseEntity<APIResponse<Long>> deleteMemberInquiry (
            @PathVariable Long inquiryId,
            @RequestBody @Valid DeleteInquiryRequestDto deleteInquiryRequestDto) {

        inquiryService.deleteInquiry(deleteInquiryRequestDto.getInquiryId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(deleteInquiryRequestDto.getInquiryId()));
    }

    @Data
    @AllArgsConstructor
    static class DeleteInquiryRequestDto {
        private Long inquiryId;
    }


    // 트레이더 문의 답변 등록 API
    @PostMapping("/trader/inquiry/{inquiryId}/write")
    public ResponseEntity<APIResponse<Long>> saveTraderInquiry (
            @PathVariable Long inquiryId,
            @RequestBody @Valid SaveInquiryDetailRequestDto saveInquiryDetailRequestDto) {

        Long inquiryAnswerId = inquiryAnswerService.registerInquiryAnswer(saveInquiryDetailRequestDto.getInquiryId(),
                saveInquiryDetailRequestDto.getAnswerContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryAnswerId));
    }


    // 트레이더 문의 조회, 검색 API
    @GetMapping("/trader/inquiry")
    public ResponseEntity<APIResponse<PageResponse<InquiryAdminShowResponseDto>>> showTraderInquiry (
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "searhType", required = false) String searhType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        InquiryShowRequestDto inquiryShowRequestDto = new InquiryShowRequestDto();
        inquiryShowRequestDto.setTraderId(1L);
        inquiryShowRequestDto.setTab(inquiryStatus);
        inquiryShowRequestDto.setSearchCondition(searhType);
        inquiryShowRequestDto.setSearchKeyword(searchText);

        long totalCountInquiry = inquiryService.totalCountByStrategyQuestionerTrader(inquiryShowRequestDto); // 전체 데이터 수
        int totalPageCount; // 전체 페이지 수
        if (totalCountInquiry % pageSize == 0) {
            totalPageCount = (int) (totalCountInquiry / pageSize);
        } else {
            totalPageCount = (int) (totalCountInquiry / pageSize) + 1;
        }
        int pageStart = (page - 1) * pageSize; // 페이지 시작 위치

        List<Inquiry> inquiryList = inquiryService.findInquiresByStrategyQuestionerTrader(inquiryShowRequestDto, pageStart, pageSize);

        List<InquiryAdminShowResponseDto> collect = inquiryList.stream()
                .map(i -> new InquiryAdminShowResponseDto(i.getId(),
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

        PageResponse<InquiryAdminShowResponseDto> traderInquiryPage = PageResponse.<InquiryAdminShowResponseDto>builder()
                .currentPage(page) // 현재 페이지
                .pageSize(pageSize) // 한 페이지 크기
                .totalElement(totalCountInquiry) // 전체 데이터 수
                .totalPages(totalPageCount) // 전체 페이지 수
                .content(collect)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(traderInquiryPage));
    }

    // 트레이더 문의 상세 조회 API
    @GetMapping("/trader/inquiry/{inquiryId}/view")
    public ResponseEntity<APIResponse<InquiryAnswerDto>> showTraderInquiryDetail (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "searhType", required = false) String searhType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        List<InquiryAnswer> inquiryAnswerList = inquiryAnswerService.findThatInquiryAnswers(inquiryId);
        InquiryAnswer inquiryAnswer = inquiryAnswerList.get(0);

        InquiryAnswerDto inquiryAnswerDto = new InquiryAnswerDto();
        inquiryAnswerDto.setId(inquiryAnswer.getId());
        inquiryAnswerDto.setInquiryId(inquiryAnswer.getInquiry().getId());
        inquiryAnswerDto.setStrategyName(inquiryAnswer.getInquiry().getStrategy().getName());
        inquiryAnswerDto.setInquiryTitle(inquiryAnswer.getInquiry().getInquiryTitle());
        inquiryAnswerDto.setInquiryContent(inquiryAnswer.getInquiry().getInquiryContent());
        inquiryAnswerDto.setMemberName(inquiryAnswer.getInquiry().getMember().getName());
        inquiryAnswerDto.setInquiryRegistrationDate(inquiryAnswer.getInquiry().getInquiryRegistrationDate());
        inquiryAnswerDto.setAnswerContent(inquiryAnswer.getAnswerContent());
        inquiryAnswerDto.setTraderName(inquiryAnswer.getInquiry().getStrategy().getTrader().getName());
        inquiryAnswerDto.setAnswerRegistrationDate(inquiryAnswer.getAnswerRegistrationDate());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryAnswerDto));
    }
}
