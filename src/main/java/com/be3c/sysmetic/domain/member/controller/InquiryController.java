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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "문의 API", description = "관리자, 트레이더, 투자자 문의 API")
@RequiredArgsConstructor
@RestController
public class InquiryController implements InquiryControllerDocs {

    private final InquiryService inquiryService;
    private final InquiryAnswerService inquiryAnswerService;

    private final Integer pageSize = 10; // 한 페이지 크기

    /*
        관리자 문의 조회 / 검색 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 데이터 조회에 성공했을 때 : OK
        3. 페이지 내에 한 개의 문의도 존재하지 않을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/admin/inquiry")
    public ResponseEntity<APIResponse<PageResponse<InquiryAdminShowResponseDto>>> showAdminInquiry (
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        InquiryAdminShowRequestDto inquiryAdminShowRequestDto = new InquiryAdminShowRequestDto();
        inquiryAdminShowRequestDto.setTab(inquiryStatus);
        inquiryAdminShowRequestDto.setSearchType(searchType);
        inquiryAdminShowRequestDto.setSearchText(searchText);

        long totalCountInquiry = inquiryService.totalCountByStrategyQuestionerTrader(inquiryAdminShowRequestDto); // 전체 데이터 수
        int totalPageCount; // 전체 페이지 수
        if (totalCountInquiry % pageSize == 0) {
            totalPageCount = (int) (totalCountInquiry / pageSize);
        } else {
            totalPageCount = (int) (totalCountInquiry / pageSize) + 1;
        }
        int pageStart = (page - 1) * pageSize; // 페이지 시작 위치

        List<Inquiry> inquiryList = inquiryService.findInquiresByStrategyQuestionerTrader(inquiryAdminShowRequestDto, pageStart, pageSize);

        List<InquiryAdminShowResponseDto> collect = inquiryList.stream()
                .map(i -> new InquiryAdminShowResponseDto(
                        i.getId(),
                        i.getStrategy().getTrader().getNickname(),
                        i.getStrategy().getName(),
                        i.getInquiryRegistrationDate(),
                        i.getMember().getNickname(),
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


    /*
        관리자 문의 상세 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의의 상세 데이터 조회에 성공했을 때 : OK
        3. 문의의 상세 데이터 조회에 실패했을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/admin/inquiry/{inquiryId}/view")
    public ResponseEntity<APIResponse<InquiryAnswerShowResponseDto>> showAdminInquiryDetail (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        List<InquiryAnswer> inquiryAnswerList = inquiryAnswerService.findThatInquiryAnswers(inquiryId);
        InquiryAnswer inquiryAnswer = inquiryAnswerList.get(0);

        InquiryAnswerShowResponseDto inquiryAnswerShowResponseDto = new InquiryAnswerShowResponseDto();

        inquiryAnswerShowResponseDto.setInquiryId(inquiryAnswer.getInquiry().getId());
        inquiryAnswerShowResponseDto.setInquiryAnswerId(inquiryAnswer.getId());

        inquiryAnswerShowResponseDto.setInquiryTitle(inquiryAnswer.getInquiry().getInquiryTitle());
        inquiryAnswerShowResponseDto.setInquiryRegistrationDate(inquiryAnswer.getInquiry().getInquiryRegistrationDate());
        inquiryAnswerShowResponseDto.setInquirerNickname(inquiryAnswer.getInquiry().getMember().getNickname());
        inquiryAnswerShowResponseDto.setInquiryStatus(inquiryAnswer.getInquiry().getInquiryStatus());

        inquiryAnswerShowResponseDto.setStrategyName(inquiryAnswer.getInquiry().getStrategy().getName());
        inquiryAnswerShowResponseDto.setTraderNickname(inquiryAnswer.getInquiry().getStrategy().getTrader().getNickname());

        inquiryAnswerShowResponseDto.setInquiryContent(inquiryAnswer.getInquiry().getInquiryContent());

        inquiryAnswerShowResponseDto.setAnswerTitle(inquiryAnswer.getAnswerTitle());
        inquiryAnswerShowResponseDto.setAnswerRegistrationDate(inquiryAnswer.getAnswerRegistrationDate());
        inquiryAnswerShowResponseDto.setAnswerContent(inquiryAnswer.getAnswerContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryAnswerShowResponseDto));
    }


    /*
        질문자 문의 등록 화면 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 질문자 문의 등록 화면 조회에 성공했을 때 : OK
        3. 질문자 문의 등록 화면 조회에 실패했을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/strategy/inquiry")
    public ResponseEntity<APIResponse<InquirySavePageShowResponseDto>> showInquirySavePage (
            @RequestBody InquirySavePageShowRequestDto inquirySavePageShowRequestDto) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(showAdminNoticeDetailResponseDto);
    }


    /*
        질문자 문의 등록 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의가 등록에 성공했을 때 : OK
        3. 문의가 등록에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
    @Override
    @PostMapping("/strategy/inquiry")
    public ResponseEntity<APIResponse<Long>> saveInquirerInquiry(
            @RequestBody InquirySaveRequestDto inquirySaveRequestDto) {

        Long inquiryId = inquiryService.registerInquiry(
                inquirySaveRequestDto.getMemberId(),
                inquirySaveRequestDto.getStrategyId(),
                inquirySaveRequestDto.getInquiryTitle(),
                inquirySaveRequestDto.getInquiryContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryId));
    }


    /*
        질문자 문의 조회 / 검색 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 데이터 조회에 성공했을 때 : OK
        3. 페이지 내에 한 개의 문의도 존재하지 않을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/member/inquiry")
    public ResponseEntity<APIResponse<PageResponse<InquiryShowResponseDto>>> showInquirerInquiry (
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "closed") String closed) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        InquiryShowRequestDto inquiryShowRequestDto = new InquiryShowRequestDto();
        inquiryShowRequestDto.setInquirerId(123L); // 현재 로그인한 회원의 아이디
        inquiryShowRequestDto.setSort(sort);
        inquiryShowRequestDto.setTab(inquiryStatus);

        long totalCountInquiry = inquiryService.totalCountByStrategyQuestionerTrader(inquiryAdminShowRequestDto); // 전체 데이터 수
        int totalPageCount; // 전체 페이지 수
        if (totalCountInquiry % pageSize == 0) {
            totalPageCount = (int) (totalCountInquiry / pageSize);
        } else {
            totalPageCount = (int) (totalCountInquiry / pageSize) + 1;
        }
        int pageStart = (page - 1) * pageSize; // 페이지 시작 위치

        List<Inquiry> inquiryList = inquiryService.findInquiresByStrategyQuestionerTrader(inquiryAdminShowRequestDto, pageStart, pageSize);

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


     /*
        질문자 문의 상세 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의의 상세 데이터 조회에 성공했을 때 : OK
        3. 문의의 상세 데이터 조회에 실패했을 때 : NOT_FOUND
     */
     @Override
     @GetMapping("/member/inquiry/{inquiryId}/view")
    public ResponseEntity<APIResponse<InquiryAnswerShowResponseDto>> showInquirerInquiryDetail (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "closed") String closed) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        List<InquiryAnswer> inquiryAnswerList = inquiryAnswerService.findThatInquiryAnswers(inquiryId);
        InquiryAnswer inquiryAnswer = inquiryAnswerList.get(0);

        InquiryAnswerShowResponseDto inquiryAnswerShowResponseDto = new InquiryAnswerShowResponseDto();
        inquiryAnswerShowResponseDto.setId(inquiryAnswer.getId());
        inquiryAnswerShowResponseDto.setInquiryId(inquiryAnswer.getInquiry().getId());
        inquiryAnswerShowResponseDto.setStrategyName(inquiryAnswer.getInquiry().getStrategy().getName());
        inquiryAnswerShowResponseDto.setInquiryTitle(inquiryAnswer.getInquiry().getInquiryTitle());
        inquiryAnswerShowResponseDto.setInquiryContent(inquiryAnswer.getInquiry().getInquiryContent());
        inquiryAnswerShowResponseDto.setMemberName(inquiryAnswer.getInquiry().getMember().getName());
        inquiryAnswerShowResponseDto.setInquiryRegistrationDate(inquiryAnswer.getInquiry().getInquiryRegistrationDate());
        inquiryAnswerShowResponseDto.setAnswerContent(inquiryAnswer.getAnswerContent());
        inquiryAnswerShowResponseDto.setTraderName(inquiryAnswer.getInquiry().getStrategy().getTrader().getName());
        inquiryAnswerShowResponseDto.setAnswerRegistrationDate(inquiryAnswer.getAnswerRegistrationDate());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryAnswerShowResponseDto));
    }


    /*
        질문자 문의 수정 화면 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 질문자 문의 수정 화면 조회에 성공했을 때 : OK
        3. 질문자 문의 수정 화면 조회에 실패했을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/member/inquiry/{inquiryId}/modify")
    public ResponseEntity<APIResponse<InquiryShowModifyPageResponseDto>> showInquiryModifyPage (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "closed") String closed) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);


        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryAnswerDto));
    }


     /*
        질문자 문의 수정 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 수정에 성공했을 때 : OK
        3. 문의 수정에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 해당 문의를 찾지 못했을 때 : NOT_FOUND
        5. 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
     @Override
     @PatchMapping("/member/inquiry/{inquiryId}/modify")
    public ResponseEntity<APIResponse<Long>> modifyInquirerInquiry (
            @PathVariable Long inquiryId,
            @RequestBody @Valid InquiryModifyRequestDto inquiryModifyRequestDto) {

        inquiryService.modifyInquiry(inquiryModifyRequestDto.getInquiryId(),
                inquiryModifyRequestDto.getInquiryTitle(),
                inquiryModifyRequestDto.getInquiryContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryModifyRequestDto.getInquiryId()));
    }


    /*
        질문자 문의 삭제 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 삭제에 성공했을 때 : OK
        3. 문의 삭제에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 해당 문의를 찾지 못했을 때 : NOT_FOUND
     */
    @Override
    @DeleteMapping("/member/inquiry/{inquiryId}")
    public ResponseEntity<APIResponse<Long>> deleteInquirerInquiry (
            @PathVariable Long inquiryId) {

        inquiryService.deleteInquiry(deleteInquiryRequestDto.getInquiryId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(deleteInquiryRequestDto.getInquiryId()));
    }


    /*
        트레이더 문의 답변 등록 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 답변이 등록에 성공했을 때 : OK
        3. 문의 답변이 등록에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
    @Override
    @PostMapping("/trader/inquiry/{inquiryId}/write")
    public ResponseEntity<APIResponse<Long>> saveTraderInquiryAnswer (
            @PathVariable Long inquiryId,
            @RequestBody @Valid InquiryDetailSaveRequestDto inquiryDetailSaveRequestDto) {

        Long inquiryAnswerId = inquiryAnswerService.registerInquiryAnswer(inquiryDetailSaveRequestDto.getInquiryId(),
                inquiryDetailSaveRequestDto.getAnswerContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryAnswerId));
    }


    /*
        트레이더 문의 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 데이터 조회에 성공했을 때 : OK
        3. 페이지 내에 한 개의 문의도 존재하지 않을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/trader/inquiry")
    public ResponseEntity<APIResponse<PageResponse<InquiryShowResponseDto>>> showTraderInquiry (
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "closed") String closed) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        InquiryAdminShowRequestDto inquiryAdminShowRequestDto = new InquiryAdminShowRequestDto();
        inquiryAdminShowRequestDto.setTraderId(1L);
        inquiryAdminShowRequestDto.setTab(inquiryStatus);
        inquiryAdminShowRequestDto.setSearchType(searchType);
        inquiryAdminShowRequestDto.setSearchText(searchText);

        long totalCountInquiry = inquiryService.totalCountByStrategyQuestionerTrader(inquiryAdminShowRequestDto); // 전체 데이터 수
        int totalPageCount; // 전체 페이지 수
        if (totalCountInquiry % pageSize == 0) {
            totalPageCount = (int) (totalCountInquiry / pageSize);
        } else {
            totalPageCount = (int) (totalCountInquiry / pageSize) + 1;
        }
        int pageStart = (page - 1) * pageSize; // 페이지 시작 위치

        List<Inquiry> inquiryList = inquiryService.findInquiresByStrategyQuestionerTrader(inquiryAdminShowRequestDto, pageStart, pageSize);

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


    /*
        트레이더 문의 상세 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의의 상세 데이터 조회에 성공했을 때 : OK
        3. 문의의 상세 데이터 조회에 실패했을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/trader/inquiry/{inquiryId}/view")
    public ResponseEntity<APIResponse<InquiryAnswerShowResponseDto>> showTraderInquiryDetail (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "closed") String closed) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        List<InquiryAnswer> inquiryAnswerList = inquiryAnswerService.findThatInquiryAnswers(inquiryId);
        InquiryAnswer inquiryAnswer = inquiryAnswerList.get(0);

        InquiryAnswerShowResponseDto inquiryAnswerShowResponseDto = new InquiryAnswerShowResponseDto();
        inquiryAnswerShowResponseDto.setId(inquiryAnswer.getId());
        inquiryAnswerShowResponseDto.setInquiryId(inquiryAnswer.getInquiry().getId());
        inquiryAnswerShowResponseDto.setStrategyName(inquiryAnswer.getInquiry().getStrategy().getName());
        inquiryAnswerShowResponseDto.setInquiryTitle(inquiryAnswer.getInquiry().getInquiryTitle());
        inquiryAnswerShowResponseDto.setInquiryContent(inquiryAnswer.getInquiry().getInquiryContent());
        inquiryAnswerShowResponseDto.setMemberName(inquiryAnswer.getInquiry().getMember().getName());
        inquiryAnswerShowResponseDto.setInquiryRegistrationDate(inquiryAnswer.getInquiry().getInquiryRegistrationDate());
        inquiryAnswerShowResponseDto.setAnswerContent(inquiryAnswer.getAnswerContent());
        inquiryAnswerShowResponseDto.setTraderName(inquiryAnswer.getInquiry().getStrategy().getTrader().getName());
        inquiryAnswerShowResponseDto.setAnswerRegistrationDate(inquiryAnswer.getAnswerRegistrationDate());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryAnswerShowResponseDto));
    }
}
