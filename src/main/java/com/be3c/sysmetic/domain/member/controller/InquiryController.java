package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.*;
import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryAnswer;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.member.service.InquiryAnswerService;
import com.be3c.sysmetic.domain.member.service.InquiryService;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class InquiryController implements InquiryControllerDocs {

    private final SecurityUtils securityUtils;

    private final InquiryService inquiryService;
    private final InquiryAnswerService inquiryAnswerService;
    private final MemberRepository memberRepository;

    private final Integer pageSize = 10; // 한 페이지 크기

    /*
        관리자 문의 조회 / 검색 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 데이터 조회에 성공했을 때 : OK
        3. 파라미터 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
    @Override
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/admin/inquiry")
    public ResponseEntity<APIResponse<PageResponse<InquiryAdminListOneShowResponseDto>>> showAdminInquiry (
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "closed", required = false, defaultValue = "all") String closed,
            @RequestParam(value = "searchType", required = false, defaultValue = "strategy") String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        if (page <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "페이지가 1 이하입니다."));
        }

        if (!(closed.equals("all") || closed.equals("closed") || closed.equals("unclosed"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
        }

        if (!(searchType.equals("strategy") || searchType.equals("trader") || searchType.equals("inquirer"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
        }

        InquiryAdminListShowRequestDto inquiryAdminListShowRequestDto = new InquiryAdminListShowRequestDto();
        inquiryAdminListShowRequestDto.setTab(inquiryStatus);
        inquiryAdminListShowRequestDto.setSearchType(searchType);
        inquiryAdminListShowRequestDto.setSearchText(searchText);

        Page<Inquiry> inquiryList = inquiryService.findInquiresAdmin(inquiryAdminListShowRequestDto, page-1);

        List<InquiryAdminListOneShowResponseDto> inquiryDtoList = inquiryList.stream()
                .map(InquiryController::inquiryToInquiryAdminOneResponseDto).collect(Collectors.toList());

        PageResponse<InquiryAdminListOneShowResponseDto> adminInquiryPage = PageResponse.<InquiryAdminListOneShowResponseDto>builder()
                .currentPage(page)
                .pageSize(pageSize)
                .totalElement(inquiryList.getTotalElements())
                .totalPages(inquiryList.getTotalPages())
                .content(inquiryDtoList)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(adminInquiryPage));
    }

    public static InquiryAdminListOneShowResponseDto inquiryToInquiryAdminOneResponseDto(Inquiry inquiry) {

        return InquiryAdminListOneShowResponseDto.builder()
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
        3. 해당 문의를 찾지 못했을 때 : NOT_FOUND
        4. 파라미터 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
    @Override
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/admin/inquiry/{inquiryId}/view")
    public ResponseEntity<APIResponse<InquiryAnswerShowResponseDto>> showAdminInquiryDetail (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "all") String closed,
            @RequestParam(value = "searchType", required = false, defaultValue = "strategy") String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        if (page <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "페이지가 1 이하입니다."));
        }

        if (!(closed.equals("all") || closed.equals("closed") || closed.equals("unclosed"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
        }

        if (!(searchType.equals("strategy") || searchType.equals("trader") || searchType.equals("inquirer"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
        }

        try {
            InquiryAnswer inquiryAnswer = inquiryAnswerService.findThatInquiryAnswer(inquiryId);
            InquiryAnswer previousInquiryAnswer = inquiryAnswerService.findThatInquiryAnswer(inquiryId-1);
            InquiryAnswer nextInquiryAnswer = inquiryAnswerService.findThatInquiryAnswer(inquiryId+1);

            InquiryAnswerShowResponseDto inquiryAnswerShowResponseDto = new InquiryAnswerShowResponseDto();

            inquiryAnswerShowResponseDto.setPage(page);
            inquiryAnswerShowResponseDto.setClosed(closed);
            inquiryAnswerShowResponseDto.setSearchType(searchType);
            inquiryAnswerShowResponseDto.setSearchText(searchText);

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

            inquiryAnswerShowResponseDto.setPreviousTitle(previousInquiryAnswer.getInquiry().getInquiryTitle());
            inquiryAnswerShowResponseDto.setPreviousWriteDate(previousInquiryAnswer.getInquiry().getInquiryRegistrationDate());
            inquiryAnswerShowResponseDto.setNextTitle(nextInquiryAnswer.getInquiry().getInquiryTitle());
            inquiryAnswerShowResponseDto.setNextWriteDate(nextInquiryAnswer.getInquiry().getInquiryRegistrationDate());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.success(inquiryAnswerShowResponseDto));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }


    /*
        관리자 문의 삭제 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 삭제에 성공했을 때 : OK
        3. 문의 삭제에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 해당 문의를 찾지 못했을 때 : NOT_FOUND
     */
    @Override
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/admin/inquiry/{inquiryId}/delete")
    public ResponseEntity<APIResponse<Long>> deleteAdminInquiry (
            @PathVariable Long inquiryId) {

        try {
            if (inquiryService.deleteAdminInquiry(inquiryId)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }


    /*
        관리자 문의 목록 삭제 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 목록 삭제에 성공했을 때 : OK
        3. 해당 문의를 찾지 못했을 때 : NOT_FOUND
        4. 문의 중 일부만 삭제에 실패했을 때 : MULTI_STATUS
     */
    @Override
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/admin/inquiry/delete")
    public ResponseEntity<APIResponse<Integer>> deleteAdminInquiryList(
            @RequestBody @Valid InquiryAdminListDeleteRequestDto noticeListDeleteRequestDto) {

        List<Long> inquiryIdList = noticeListDeleteRequestDto.getInquiryIdList();

        try {
            Integer deleteCount = inquiryService.deleteAdminInquiryList(inquiryIdList);

            if (deleteCount == inquiryIdList.size()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success(deleteCount));
            }
            return ResponseEntity.status(HttpStatus.MULTI_STATUS)
                    .body(APIResponse.fail(ErrorCode.MULTI_STATUS));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }


    /*
        질문자 문의 등록 화면 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 질문자 문의 등록 화면 조회에 성공했을 때 : OK
        3. 질문자 문의 등록 화면 조회에 실패했을 때 : NOT_FOUND
     */
    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/strategy/{strategyId}/inquiry")
    public ResponseEntity<APIResponse<InquirySavePageShowResponseDto>> showInquirySavePage (
            @PathVariable Long strategyId,
            @RequestBody InquirySavePageShowRequestDto inquirySavePageShowRequestDto) {

        try {
            Strategy strategy = inquiryService.findStrategyForInquiryPage(inquirySavePageShowRequestDto.getStrategyId());

            InquirySavePageShowResponseDto inquirySavePageShowResponseDto = InquirySavePageShowResponseDto.builder()
                    .strategyName(strategy.getName())
                    .traderNickname(strategy.getTrader().getNickname())
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.success(inquirySavePageShowResponseDto));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }


    /*
        질문자 문의 등록 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의가 등록에 성공했을 때 : OK
        3. 문의가 등록에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 데이터의 형식이 올바르지 않음 : BAD_REQUEST
        5. 등록하는 질문자나 해당 전략의 정보를 찾지 못했을 때 : NOT_FOUND
     */
    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/strategy/{strategyId}/inquiry")
    public ResponseEntity<APIResponse<Long>> saveInquirerInquiry(
            @PathVariable Long strategyId,
            @RequestBody @Valid InquirySaveRequestDto inquirySaveRequestDto) {

        Long userId = securityUtils.getUserIdInSecurityContext();

        try {
            if (inquiryService.registerInquiry(
                    userId,
                    strategyId,
                    inquirySaveRequestDto.getInquiryTitle(),
                    inquirySaveRequestDto.getInquiryContent())) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }


    /*
        질문자 문의 조회 / 검색 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 데이터 조회에 성공했을 때 : OK
        3. 파라미터 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/member/inquiry")
    public ResponseEntity<APIResponse<PageResponse<InquiryListOneShowResponseDto>>> showInquirerInquiry (
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort", defaultValue = "registrationDate") String sort,
            @RequestParam(value = "closed", defaultValue = "all") String closed) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        if (page <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "페이지가 1 이하입니다."));
        }

        if (!(closed.equals("all") || closed.equals("closed") || closed.equals("unclosed"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
        }

        if (!(sort.equals("registrationDate") || sort.equals("strategyName"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
        }

        InquiryListShowRequestDto inquiryListShowRequestDto = new InquiryListShowRequestDto();
        inquiryListShowRequestDto.setInquirerId(123L); // 현재 로그인한 회원의 아이디
        inquiryListShowRequestDto.setSort(sort);
        inquiryListShowRequestDto.setTab(inquiryStatus);

        Page<Inquiry> inquiryList = inquiryService.findInquires(inquiryListShowRequestDto, page-1);

        List<InquiryListOneShowResponseDto> inquiryDtoList = inquiryList.stream()
                .map(InquiryController::inquiryToInquiryOneResponseDto).collect(Collectors.toList());

        PageResponse<InquiryListOneShowResponseDto> inquiryPage = PageResponse.<InquiryListOneShowResponseDto>builder()
                .currentPage(page)
                .pageSize(pageSize)
                .totalElement(inquiryList.getTotalElements())
                .totalPages(inquiryList.getTotalPages())
                .content(inquiryDtoList)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryPage));
    }

    public static InquiryListOneShowResponseDto inquiryToInquiryOneResponseDto(Inquiry inquiry) {

        return InquiryListOneShowResponseDto.builder()
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
        3. 해당 문의를 찾지 못했을 때 : NOT_FOUND
        4. 파라미터 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
     @Override
     @PreAuthorize("hasRole('ROLE_USER')")
     @GetMapping("/member/inquiry/{inquiryId}/view")
    public ResponseEntity<APIResponse<InquiryAnswerShowResponseDto>> showInquirerInquiryDetail (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort", defaultValue = "registrationDate") String sort,
            @RequestParam(value = "closed", defaultValue = "all") String closed) {
         InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

         if (page <= 0) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                     .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "페이지가 1 이하입니다."));
         }

         if (!(closed.equals("all") || closed.equals("closed") || closed.equals("unclosed"))) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                     .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
         }

         if (!(sort.equals("registrationDate") || sort.equals("strategyName"))) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                     .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
         }

         try {
             InquiryAnswer inquiryAnswer = inquiryAnswerService.findThatInquiryAnswer(inquiryId);
             InquiryAnswer previousInquiryAnswer = inquiryAnswerService.findThatInquiryAnswer(inquiryId-1);
             InquiryAnswer nextInquiryAnswer = inquiryAnswerService.findThatInquiryAnswer(inquiryId+1);

             InquiryAnswerShowResponseDto inquiryAnswerShowResponseDto = new InquiryAnswerShowResponseDto();

             inquiryAnswerShowResponseDto.setPage(page);
             inquiryAnswerShowResponseDto.setSort(sort);
             inquiryAnswerShowResponseDto.setClosed(closed);

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

             inquiryAnswerShowResponseDto.setPreviousTitle(previousInquiryAnswer.getInquiry().getInquiryTitle());
             inquiryAnswerShowResponseDto.setPreviousWriteDate(previousInquiryAnswer.getInquiry().getInquiryRegistrationDate());
             inquiryAnswerShowResponseDto.setNextTitle(nextInquiryAnswer.getInquiry().getInquiryTitle());
             inquiryAnswerShowResponseDto.setNextWriteDate(nextInquiryAnswer.getInquiry().getInquiryRegistrationDate());

             return ResponseEntity.status(HttpStatus.OK)
                     .body(APIResponse.success(inquiryAnswerShowResponseDto));
         }
         catch (EntityNotFoundException e) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND)
                     .body(APIResponse.fail(ErrorCode.NOT_FOUND));
         }
    }


    /*
        질문자 문의 수정 화면 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 질문자 문의 수정 화면 조회에 성공했을 때 : OK
        3. 해당 문의를 찾지 못했을 때 : NOT_FOUND
        4. 파라미터 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/member/inquiry/{inquiryId}/modify")
    public ResponseEntity<APIResponse<InquiryModifyPageShowResponseDto>> showInquiryModifyPage (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort", defaultValue = "registrationDate") String sort,
            @RequestParam(value = "closed", defaultValue = "all") String closed) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        try {
            if (page <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "페이지가 1 이하입니다."));
            }

            if (!(closed.equals("all") || closed.equals("closed") || closed.equals("unclosed"))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
            }

            if (!(sort.equals("registrationDate") || sort.equals("strategyName"))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
            }

            Inquiry inquiry = inquiryService.findOneInquiry(inquiryId);

            if(securityUtils.getUserIdInSecurityContext() != inquiry.getInquirer().getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(APIResponse.fail(ErrorCode.FORBIDDEN, "유효하지 않은 회원입니다."));
            }

            InquiryModifyPageShowResponseDto inquiryModifyPageShowResponseDto = InquiryModifyPageShowResponseDto.builder()
                    .page(page)
                    .sort(sort)
                    .closed(closed)
                    .inquiryTitle(inquiry.getInquiryTitle())
                    .inquiryContent(inquiry.getInquiryContent())
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.success(inquiryModifyPageShowResponseDto));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }


     /*
        질문자 문의 수정 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 수정에 성공했을 때 : OK
        3. 문의 수정에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 해당 문의를 찾지 못했을 때 : NOT_FOUND
        5. 데이터의 형식이 올바르지 않음 : BAD_REQUEST
            +) 답변이 등록된 문의를 수정 시도함
     */
     @Override
     @PreAuthorize("hasRole('ROLE_USER')")
     @PutMapping("/member/inquiry/{inquiryId}/modify")
    public ResponseEntity<APIResponse<Long>> modifyInquirerInquiry (
            @PathVariable Long inquiryId,
            @RequestBody @Valid InquiryModifyRequestDto inquiryModifyRequestDto) {

         Inquiry inquiry = inquiryService.findOneInquiry(inquiryId);

         if(securityUtils.getUserIdInSecurityContext() != inquiry.getInquirer().getId()) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN)
                     .body(APIResponse.fail(ErrorCode.FORBIDDEN, "유효하지 않은 회원입니다."));
         }

        try {
            if (inquiryService.modifyInquiry(
                    inquiryId,
                    inquiryModifyRequestDto.getInquiryTitle(),
                    inquiryModifyRequestDto.getInquiryContent())) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        }
        catch (IllegalStateException e) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                     .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "답변이 등록된 문의는 수정할 수 없습니다."));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }


    /*
        질문자 문의 삭제 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 삭제에 성공했을 때 : OK
        3. 문의 삭제에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 해당 문의를 찾지 못했을 때 : NOT_FOUND
        5. 답변이 등록된 문의를 수정 시도함 : BAD_REQUEST
     */
    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/member/inquiry/{inquiryId}/delete")
    public ResponseEntity<APIResponse<Long>> deleteInquirerInquiry (
            @PathVariable Long inquiryId) {

        Inquiry inquiry = inquiryService.findOneInquiry(inquiryId);

        if(!Objects.equals(securityUtils.getUserIdInSecurityContext(), inquiry.getInquirer().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(APIResponse.fail(ErrorCode.FORBIDDEN, "유효하지 않은 회원입니다."));
        }

        try {
            if (inquiryService.deleteInquiry(inquiryId)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
        catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "답변이 등록된 문의는 수정할 수 없습니다."));
        }
    }


    /*
        트레이더 문의 답변 등록 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 답변이 등록에 성공했을 때 : OK
        3. 문의 답변이 등록에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 데이터의 형식이 올바르지 않음 : BAD_REQUEST
        5. 등록하는 문의 정보를 찾지 못했을 때 : NOT_FOUND
     */
    @Override
    @PreAuthorize("hasRole('ROLE_TRADER')")
    @PostMapping("/trader/inquiry/{inquiryId}/write")
    public ResponseEntity<APIResponse<Long>> saveTraderInquiryAnswer (
            @PathVariable Long inquiryId,
            @RequestBody @Valid InquiryDetailSaveRequestDto inquiryDetailSaveRequestDto) {

        try {
            if (inquiryAnswerService.registerInquiryAnswer(
                    inquiryId,
                    inquiryDetailSaveRequestDto.getAnswerTitle(),
                    inquiryDetailSaveRequestDto.getAnswerContent())) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }


    /*
        트레이더 문의 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의 데이터 조회에 성공했을 때 : OK
        3. 파라미터 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
    @Override
    @PreAuthorize("hasRole('ROLE_TRADER')")
    @GetMapping("/trader/inquiry")
    public ResponseEntity<APIResponse<PageResponse<InquiryListOneShowResponseDto>>> showTraderInquiry (
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort", defaultValue = "registrationDate") String sort,
            @RequestParam(value = "closed", defaultValue = "all") String closed) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        if (page <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "페이지가 1 이하입니다."));
        }

        if (!(closed.equals("all") || closed.equals("closed") || closed.equals("unclosed"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
        }

        if (!(sort.equals("registrationDate") || sort.equals("strategyName"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
        }

        InquiryListShowRequestDto inquiryListShowRequestDto = new InquiryListShowRequestDto();
        inquiryListShowRequestDto.setInquirerId(123L); // 현재 로그인한 회원의 아이디
        inquiryListShowRequestDto.setSort(sort);
        inquiryListShowRequestDto.setTab(inquiryStatus);

        Page<Inquiry> inquiryList = inquiryService.findInquires(inquiryListShowRequestDto, page-1);

        List<InquiryListOneShowResponseDto> inquiryDtoList = inquiryList.stream()
                .map(InquiryController::inquiryToInquiryOneResponseDto).collect(Collectors.toList());

        PageResponse<InquiryListOneShowResponseDto> inquiryPage = PageResponse.<InquiryListOneShowResponseDto>builder()
                .currentPage(page)
                .pageSize(pageSize)
                .totalElement(inquiryList.getTotalElements())
                .totalPages(inquiryList.getTotalPages())
                .content(inquiryDtoList)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryPage));
    }


    /*
        트레이더 문의 상세 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 문의의 상세 데이터 조회에 성공했을 때 : OK
        3. 해당 문의를 찾지 못했을 때 : NOT_FOUND
     */
    @Override
    @PreAuthorize("hasRole('ROLE_TRADER')")
    @GetMapping("/trader/inquiry/{inquiryId}/view")
    public ResponseEntity<APIResponse<InquiryAnswerShowResponseDto>> showTraderInquiryDetail (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort", defaultValue = "registrationDate") String sort,
            @RequestParam(value = "closed", defaultValue = "all") String closed) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        if (page <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "페이지가 1 이하입니다."));
        }

        if (!(closed.equals("all") || closed.equals("closed") || closed.equals("unclosed"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
        }

        if (!(sort.equals("registrationDate") || sort.equals("strategyName"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "쿼리 파라미터 searchType이 올바르지 않습니다."));
        }

        try {
            InquiryAnswer inquiryAnswer = inquiryAnswerService.findThatInquiryAnswer(inquiryId);
            InquiryAnswer previousInquiryAnswer = inquiryAnswerService.findThatInquiryAnswer(inquiryId-1);
            InquiryAnswer nextInquiryAnswer = inquiryAnswerService.findThatInquiryAnswer(inquiryId+1);

            InquiryAnswerShowResponseDto inquiryAnswerShowResponseDto = new InquiryAnswerShowResponseDto();

            inquiryAnswerShowResponseDto.setPage(page);
            inquiryAnswerShowResponseDto.setSort(sort);
            inquiryAnswerShowResponseDto.setClosed(closed);

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

            inquiryAnswerShowResponseDto.setPreviousTitle(previousInquiryAnswer.getInquiry().getInquiryTitle());
            inquiryAnswerShowResponseDto.setPreviousWriteDate(previousInquiryAnswer.getInquiry().getInquiryRegistrationDate());
            inquiryAnswerShowResponseDto.setNextTitle(nextInquiryAnswer.getInquiry().getInquiryTitle());
            inquiryAnswerShowResponseDto.setNextWriteDate(nextInquiryAnswer.getInquiry().getInquiryRegistrationDate());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.success(inquiryAnswerShowResponseDto));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }
}
