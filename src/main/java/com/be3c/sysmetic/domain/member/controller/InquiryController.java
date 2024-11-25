package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.*;
import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryAnswer;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.service.InquiryAnswerService;
import com.be3c.sysmetic.domain.member.service.InquiryService;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.global.common.response.APIResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<APIResponse<InquiryAdminListShowResponseDto>> showAdminInquiry (
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "closed", required = false, defaultValue = "all") String closed,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        InquiryAdminListShowRequestDto inquiryAdminListShowRequestDto = new InquiryAdminListShowRequestDto();
        inquiryAdminListShowRequestDto.setTab(inquiryStatus);
        inquiryAdminListShowRequestDto.setSearchType(searchType);
        inquiryAdminListShowRequestDto.setSearchText(searchText);

        Page<Inquiry> inquiryList = inquiryService.findInquiresAdmin(inquiryAdminListShowRequestDto, page-1);

        List<InquiryAdminOneShowResponseDto> inquiryDtoList = inquiryList.stream()
                .map(InquiryController::inquiryToInquiryAdminOneResponseDto).collect(Collectors.toList());

        InquiryAdminListShowResponseDto inquiryListDto = InquiryAdminListShowResponseDto.builder()
                .inquiryAdminList(inquiryDtoList)
                .listSize(inquiryDtoList.size())
                .totalPage(inquiryList.getTotalPages())
                .totalElements(inquiryList.getTotalElements())
                .isFirst(inquiryList.isFirst())
                .isLast(inquiryList.isLast())
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryListDto));
    }

    public static InquiryAdminOneShowResponseDto inquiryToInquiryAdminOneResponseDto(Inquiry inquiry) {

        return InquiryAdminOneShowResponseDto.builder()
                .inquiryId(inquiry.getId())
                .traderNickname(inquiry.getStrategy().getTrader().getNickname())
                .strategyName(inquiry.getStrategy().getName())
                .inquiryRegistrationDate(inquiry.getInquiryRegistrationDate())
                .inquirerNickname(inquiry.getInquirer().getNickname())
                .inquiryStatus(inquiry.getInquiryStatus())
                .build();
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
            @RequestParam(value = "closed", required = false, defaultValue = "all") String closed,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        InquiryAnswer inquiryAnswer = inquiryAnswerService.findThatInquiryAnswer(inquiryId);

        InquiryAnswerShowResponseDto inquiryAnswerShowResponseDto = new InquiryAnswerShowResponseDto();

        inquiryAnswerShowResponseDto.setInquiryId(inquiryAnswer.getInquiry().getId());
        inquiryAnswerShowResponseDto.setInquiryAnswerId(inquiryAnswer.getId());

        inquiryAnswerShowResponseDto.setInquiryTitle(inquiryAnswer.getInquiry().getInquiryTitle());
        inquiryAnswerShowResponseDto.setInquiryRegistrationDate(inquiryAnswer.getInquiry().getInquiryRegistrationDate());
        inquiryAnswerShowResponseDto.setInquirerNickname(inquiryAnswer.getInquiry().getInquirer().getNickname());
        inquiryAnswerShowResponseDto.setInquiryStatus(inquiryAnswer.getInquiry().getInquiryStatus());

        // 전략 위 아이콘들
        inquiryAnswerShowResponseDto.setStrategyName(inquiryAnswer.getInquiry().getStrategy().getName());

        // 트레이더의 아이콘
        inquiryAnswerShowResponseDto.setTraderNickname(inquiryAnswer.getInquiry().getStrategy().getTrader().getNickname());

        inquiryAnswerShowResponseDto.setInquiryContent(inquiryAnswer.getInquiry().getInquiryContent());

        inquiryAnswerShowResponseDto.setAnswerTitle(inquiryAnswer.getAnswerTitle());
        inquiryAnswerShowResponseDto.setAnswerRegistrationDate(inquiryAnswer.getAnswerRegistrationDate());
        inquiryAnswerShowResponseDto.setAnswerContent(inquiryAnswer.getAnswerContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryAnswerShowResponseDto));
    }


    /*
        관리자 문의 삭제 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 삭제에 성공했을 때 : OK
        3. 문의 삭제에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 해당 문의를 찾지 못했을 때 : NOT_FOUND
     */
    @Override
    @DeleteMapping("/admin/inquiry/{inquiryId}/delete")
    public ResponseEntity<APIResponse<Long>> deleteAdminInquiry (
            @PathVariable Long inquiryId) {

        inquiryService.deleteAdminInquiry(inquiryId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }


    /*
        관리자 문의 목록 삭제 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 목록 삭제에 성공했을 때 : OK
        3. 문의 목록 삭제에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 해당 문의를 찾지 못했을 때 : NOT_FOUND
     */
    @Override
    @DeleteMapping("/admin/inquiry/delete")
    public ResponseEntity<APIResponse<Integer>> deleteAdminInquiryList(
            @RequestBody @Valid InquiryAdminListDeleteRequestDto noticeListDeleteRequestDto) {

        List<Long> inquiryIdList = noticeListDeleteRequestDto.getInquiryIdList();

        inquiryService.deleteAdminInquiryList(inquiryIdList);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryIdList.size()));
    }


    /*
        질문자 문의 등록 화면 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 질문자 문의 등록 화면 조회에 성공했을 때 : OK
        3. 질문자 문의 등록 화면 조회에 실패했을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/strategy/{strategyId}/inquiry")
    public ResponseEntity<APIResponse<InquirySavePageShowResponseDto>> showInquirySavePage (
            @RequestBody InquirySavePageShowRequestDto inquirySavePageShowRequestDto) {

        Strategy strategy = inquiryService.findStrategyForInquiryPage(inquirySavePageShowRequestDto.getStrategyId());

        InquirySavePageShowResponseDto inquirySavePageShowResponseDto = InquirySavePageShowResponseDto.builder()
                .strategyName(strategy.getName())
                .traderNickname(strategy.getTrader().getNickname())
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquirySavePageShowResponseDto));
    }


    /*
        질문자 문의 등록 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의가 등록에 성공했을 때 : OK
        3. 문의가 등록에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
    @Override
    @PostMapping("/strategy/{strategyId}/inquiry")
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
    public ResponseEntity<APIResponse<InquiryListShowResponseDto>> showInquirerInquiry (
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "closed") String closed) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        InquiryListShowRequestDto inquiryListShowRequestDto = new InquiryListShowRequestDto();
        inquiryListShowRequestDto.setInquirerId(123L); // 현재 로그인한 회원의 아이디
        inquiryListShowRequestDto.setSort(sort);
        inquiryListShowRequestDto.setTab(inquiryStatus);

        Page<Inquiry> inquiryList = inquiryService.findInquires(inquiryListShowRequestDto, page-1);

        List<InquiryOneShowResponseDto> inquiryDtoList = inquiryList.stream()
                .map(InquiryController::inquiryToInquiryOneResponseDto).collect(Collectors.toList());

        InquiryListShowResponseDto inquiryListDto = InquiryListShowResponseDto.builder()
                .inquiryList(inquiryDtoList)
                .listSize(inquiryDtoList.size())
                .totalPage(inquiryList.getTotalPages())
                .totalElements(inquiryList.getTotalElements())
                .isFirst(inquiryList.isFirst())
                .isLast(inquiryList.isLast())
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryListDto));
    }

    public static InquiryOneShowResponseDto inquiryToInquiryOneResponseDto(Inquiry inquiry) {

        return InquiryOneShowResponseDto.builder()
                .inquiryId(inquiry.getId())
                .inquiryTitle(inquiry.getInquiryTitle())
                .strategyName(inquiry.getStrategy().getName())
                .inquiryRegistrationDate(inquiry.getInquiryRegistrationDate())
                .inquiryStatus(inquiry.getInquiryStatus())
                .build();
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

         InquiryAnswer inquiryAnswer = inquiryAnswerService.findThatInquiryAnswer(inquiryId);

         InquiryAnswerShowResponseDto inquiryAnswerShowResponseDto = new InquiryAnswerShowResponseDto();

         inquiryAnswerShowResponseDto.setInquiryId(inquiryAnswer.getInquiry().getId());
         inquiryAnswerShowResponseDto.setInquiryAnswerId(inquiryAnswer.getId());

         inquiryAnswerShowResponseDto.setInquiryTitle(inquiryAnswer.getInquiry().getInquiryTitle());
         inquiryAnswerShowResponseDto.setInquiryRegistrationDate(inquiryAnswer.getInquiry().getInquiryRegistrationDate());
         inquiryAnswerShowResponseDto.setInquirerNickname(inquiryAnswer.getInquiry().getInquirer().getNickname());
         inquiryAnswerShowResponseDto.setInquiryStatus(inquiryAnswer.getInquiry().getInquiryStatus());

         // 전략 위 아이콘들
         inquiryAnswerShowResponseDto.setStrategyName(inquiryAnswer.getInquiry().getStrategy().getName());

         // 트레이더의 아이콘
         inquiryAnswerShowResponseDto.setTraderNickname(inquiryAnswer.getInquiry().getStrategy().getTrader().getNickname());

         inquiryAnswerShowResponseDto.setInquiryContent(inquiryAnswer.getInquiry().getInquiryContent());

         inquiryAnswerShowResponseDto.setAnswerTitle(inquiryAnswer.getAnswerTitle());
         inquiryAnswerShowResponseDto.setAnswerRegistrationDate(inquiryAnswer.getAnswerRegistrationDate());
         inquiryAnswerShowResponseDto.setAnswerContent(inquiryAnswer.getAnswerContent());

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
    public ResponseEntity<APIResponse<InquiryModifyPageShowResponseDto>> showInquiryModifyPage (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "closed") String closed) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        Inquiry inquiry = inquiryService.findOneInquiry(inquiryId);

        InquiryModifyPageShowResponseDto inquiryModifyPageShowResponseDto = InquiryModifyPageShowResponseDto.builder()
                .inquiryTitle(inquiry.getInquiryTitle())
                .inquiryContent(inquiry.getInquiryContent())
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryModifyPageShowResponseDto));
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
     @PutMapping("/member/inquiry/{inquiryId}/modify")
    public ResponseEntity<APIResponse<Long>> modifyInquirerInquiry (
            @PathVariable Long inquiryId,
            @RequestBody @Valid InquiryModifyRequestDto inquiryModifyRequestDto) {

        inquiryService.modifyInquiry(inquiryId,
                inquiryModifyRequestDto.getInquiryTitle(),
                inquiryModifyRequestDto.getInquiryContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }


    /*
        질문자 문의 삭제 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 삭제에 성공했을 때 : OK
        3. 문의 삭제에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 해당 문의를 찾지 못했을 때 : NOT_FOUND
     */
    @Override
    @DeleteMapping("/member/inquiry/{inquiryId}/delete")
    public ResponseEntity<APIResponse<Long>> deleteInquirerInquiry (
            @PathVariable Long inquiryId) {

        inquiryService.deleteInquiry(inquiryId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
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

        Long inquiryAnswerId = inquiryAnswerService.registerInquiryAnswer(
                inquiryId,
                inquiryDetailSaveRequestDto.getAnswerTitle(),
                inquiryDetailSaveRequestDto.getAnswerContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }


    /*
        트레이더 문의 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 데이터 조회에 성공했을 때 : OK
        3. 페이지 내에 한 개의 문의도 존재하지 않을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/trader/inquiry")
    public ResponseEntity<APIResponse<InquiryListShowResponseDto>> showTraderInquiry (
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "closed") String closed) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        InquiryListShowRequestDto inquiryListShowRequestDto = new InquiryListShowRequestDto();
        inquiryListShowRequestDto.setInquirerId(123L); // 현재 로그인한 회원의 아이디
        inquiryListShowRequestDto.setSort(sort);
        inquiryListShowRequestDto.setTab(inquiryStatus);

        Page<Inquiry> inquiryList = inquiryService.findInquires(inquiryListShowRequestDto, page-1);

        List<InquiryOneShowResponseDto> inquiryDtoList = inquiryList.stream()
                .map(InquiryController::inquiryToInquiryOneResponseDto).collect(Collectors.toList());

        InquiryListShowResponseDto inquiryListDto = InquiryListShowResponseDto.builder()
                .inquiryList(inquiryDtoList)
                .listSize(inquiryDtoList.size())
                .totalPage(inquiryList.getTotalPages())
                .totalElements(inquiryList.getTotalElements())
                .isFirst(inquiryList.isFirst())
                .isLast(inquiryList.isLast())
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryListDto));
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

        InquiryAnswer inquiryAnswer = inquiryAnswerService.findThatInquiryAnswer(inquiryId);

        InquiryAnswerShowResponseDto inquiryAnswerShowResponseDto = new InquiryAnswerShowResponseDto();

        inquiryAnswerShowResponseDto.setInquiryId(inquiryAnswer.getInquiry().getId());
        inquiryAnswerShowResponseDto.setInquiryAnswerId(inquiryAnswer.getId());

        inquiryAnswerShowResponseDto.setInquiryTitle(inquiryAnswer.getInquiry().getInquiryTitle());
        inquiryAnswerShowResponseDto.setInquiryRegistrationDate(inquiryAnswer.getInquiry().getInquiryRegistrationDate());
        inquiryAnswerShowResponseDto.setInquirerNickname(inquiryAnswer.getInquiry().getInquirer().getNickname());
        inquiryAnswerShowResponseDto.setInquiryStatus(inquiryAnswer.getInquiry().getInquiryStatus());

        // 전략 위 아이콘들
        inquiryAnswerShowResponseDto.setStrategyName(inquiryAnswer.getInquiry().getStrategy().getName());

        // 트레이더의 아이콘
        inquiryAnswerShowResponseDto.setTraderNickname(inquiryAnswer.getInquiry().getStrategy().getTrader().getNickname());

        inquiryAnswerShowResponseDto.setInquiryContent(inquiryAnswer.getInquiry().getInquiryContent());

        inquiryAnswerShowResponseDto.setAnswerTitle(inquiryAnswer.getAnswerTitle());
        inquiryAnswerShowResponseDto.setAnswerRegistrationDate(inquiryAnswer.getAnswerRegistrationDate());
        inquiryAnswerShowResponseDto.setAnswerContent(inquiryAnswer.getAnswerContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryAnswerShowResponseDto));
    }
}
