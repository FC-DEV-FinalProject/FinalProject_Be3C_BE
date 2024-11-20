package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.*;
import com.be3c.sysmetic.domain.member.entity.Inquiry;
import com.be3c.sysmetic.domain.member.entity.InquiryAnswer;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import com.be3c.sysmetic.domain.member.service.InquiryAnswerService;
import com.be3c.sysmetic.domain.member.service.InquiryService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    // 관리자 문의 조회, 검색 API
    @Operation(
            summary = "관리자 문의 조회, 검색",
            description = "관리자가 문의를 조회, 검색하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "관리자 문의 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "문의가 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            )
    })
    @GetMapping("/admin/inquiry")
    public ResponseEntity<APIResponse<PageResponse<InquiryDto>>> showAdminInquiry(
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
                .body(APIResponse.success(adminInquiryPage));
    }


    // 관리자 문의 상세 조회 API
    @Operation(
            summary = "관리자 문의 상세 조회",
            description = "관리자가 문의를 상세 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "관리자 문의 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "문의가 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            )
    })
    @GetMapping("/admin/inquiry/view")
    public ResponseEntity<APIResponse<InquiryAnswerDto>> showAdminInquiryDetail(
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


    // 질문자 문의 조회, 검색 API
    @Operation(
            summary = "질문자 문의 조회, 검색",
            description = "질문자가 문의를 조회, 검색하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "질문자 문의 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않음 (UNAUTHRORIZED)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "문의가 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            )
    })
    @GetMapping("/member/inquiry")
    public ResponseEntity<APIResponse<PageResponse<InquiryDto>>> showMemberInquiry(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "search_condition", required = false) String searchCondition,
            @RequestParam(value = "search_keyword", required = false) String searchKeyword) {
        InquiryStatus inquiryStatus = InquiryStatus.valueOf(closed);

        ShowInquiryRequestDto showInquiryRequestDto = new ShowInquiryRequestDto();
        showInquiryRequestDto.setMemberId(123L); // 현재 로그인한 회원의 아이디
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
                .body(APIResponse.success(memberInquiryPage));
    }


    // 질문자 문의 상세 조회 API
    @Operation(
            summary = "질문자 문의 상세 조회",
            description = "질문자가 문의를 상세 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "질문자 문의 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않음 (UNAUTHRORIZED)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "문의가 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            )
    })
    @GetMapping("/member/inquiry/view")
    public ResponseEntity<APIResponse<InquiryAnswerDto>> showMemberInquiryDetail(
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
    @Operation(
            summary = "질문자 문의 등록",
            description = "질문자가 문의를 등록하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "질문자 문의 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않음 (UNAUTHRORIZED)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            )
    })
    @PostMapping("/member/inquiry")
    public ResponseEntity<APIResponse<Long>> saveMemberInquiry(
            @RequestBody SaveInquiryRequestDto saveInquiryRequestDto) {

        Long inquiryId = inquiryService.registerInquiry(saveInquiryRequestDto.getMemberId(),
                saveInquiryRequestDto.getStrategyId(),
                saveInquiryRequestDto.getInquiryTitle(),
                saveInquiryRequestDto.getInquiryContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryId));
    }


    // 질문자 문의 수정 API
    @Operation(
            summary = "질문자 문의 수정",
            description = "질문자가 문의를 수정하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "질문자 문의 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않음 (UNAUTHRORIZED)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "문의가 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            )
    })
    @PutMapping("/member/inquiry")
    public ResponseEntity<APIResponse<Long>> modifyMemberInquiry(
            @RequestBody @Valid ModifyInquiryRequestDto modifyInquiryRequestDto) {

        inquiryService.modifyInquiry(modifyInquiryRequestDto.getInquiryId(),
                modifyInquiryRequestDto.getInquiryTitle(),
                modifyInquiryRequestDto.getInquiryContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(modifyInquiryRequestDto.getInquiryId()));
    }


    // 질문자 문의 삭제 API
    @Operation(
            summary = "질문자 문의 삭제",
            description = "질문자가 문의를 삭제하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "질문자 문의 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않음 (UNAUTHRORIZED)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "문의가 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            )
    })
    @DeleteMapping("/member/inquiry")
    public ResponseEntity<APIResponse<Long>> deleteMemberInquiry(
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
    @Operation(
            summary = "트레이더 문의 답변 등록",
            description = "트레이더가 문의 답변을 등록하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "트레이더 문의 답변 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않음 (UNAUTHRORIZED)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            )
    })
    @PostMapping("/trader/inquiry")
    public ResponseEntity<APIResponse<Long>> saveTraderInquiry(
            @RequestBody @Valid SaveInquiryDetailRequestDto saveInquiryDetailRequestDto) {

        Long inquiryAnswerId = inquiryAnswerService.registerInquiryAnswer(saveInquiryDetailRequestDto.getInquiryId(),
                saveInquiryDetailRequestDto.getAnswerContent());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(inquiryAnswerId));
    }


    // 트레이더 문의 조회, 검색 API
    @Operation(
            summary = "트레이더 문의 조회, 검색",
            description = "트레이더가 문의를 조회, 검색하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "트레이더 문의 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않음 (UNAUTHRORIZED)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "문의가 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            )
    })
    @GetMapping("/trader/inquiry")
    public ResponseEntity<APIResponse<PageResponse<InquiryDto>>> showTraderInquiry(
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
                .body(APIResponse.success(traderInquiryPage));
    }

    // 트레이더 문의 상세 조회 API
    @Operation(
            summary = "트레이더 문의 상세 조회",
            description = "트레이더가 문의를 상세 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "트레이더 문의 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않음 (UNAUTHRORIZED)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "문의가 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorCode.class))
            )
    })
    @GetMapping("/trader/inquiry/view")
    public ResponseEntity<APIResponse<InquiryAnswerDto>> showTraderInquiryDetail(
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
