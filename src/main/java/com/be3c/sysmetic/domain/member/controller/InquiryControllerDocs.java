package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.*;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "문의 API", description = "관리자, 트레이더, 투자자 문의 API")
public interface InquiryControllerDocs {


    // 관리자 문의 조회 / 검색 API
    @Operation(
            summary = "관리자 문의 조회 / 검색",
            description = "관리자가 문의를 조회하거나 검색하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "문의 데이터 조회에 성공 (OK)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "페이지 내에 한 개의 문의도 존재하지 않을 때 (NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            )
    })
    @Parameters({
            @Parameter(name = "closed", description = "답변 상태 탭 (사용: all, closed, unclosed) (설명: 전체, 답변완료, 답변대기)"),
            @Parameter(name = "searchType", description = "검색 유형 (사용: strategy, trader, inquirer) (설명: 전략명, 트레이더, 질문자)")
    })
    @GetMapping("/admin/inquiry")
    public ResponseEntity<APIResponse<PageResponse<InquiryAdminListOneShowResponseDto>>> showAdminInquiry (
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText);


    // 관리자 문의 상세 조회 API
    @Operation(
            summary = "관리자 문의 상세 조회",
            description = "관리자가 문의의 상세 정보를 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "문의의 상세 데이터 조회에 성공 (OK)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "문의의 상세 데이터 조회에 실패 (NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            )
    })
    @Parameters({
            @Parameter(name = "closed", description = "답변 상태 탭 (사용: all, closed, unclosed) (설명: 전체, 답변완료, 답변대기)"),
            @Parameter(name = "searchType", description = "검색 유형 (사용: strategy, trader, inquirer) (설명: 전략명, 트레이더, 질문자)")
    })
    @GetMapping("/admin/inquiry/{inquiryId}/view")
    public ResponseEntity<APIResponse<InquiryAnswerShowResponseDto>> showAdminInquiryDetail (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "closed", required = false, defaultValue = "ALL") String closed,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText);


    // 관리자 문의 삭제 API
    @Operation(
            summary = "관리자 문의 삭제",
            description = "관리자가 특정 문의를 삭제하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)"
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "문의 삭제 성공 (OK)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "문의 삭제 실패 (INTERNAL_SERVER_ERROR)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 문의를 찾지 못함 (NOT_FOUND)"
            )
    })
    @DeleteMapping("/admin/inquiry/{inquiryId}/delete")
    public ResponseEntity<APIResponse<Long>> deleteAdminInquiry (
            @PathVariable Long inquiryId);


    // 관리자 문의 목록 삭제 API
    @Operation(
            summary = "관리자 문의 목록 삭제",
            description = "관리자가 다수의 문의를 삭제하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)"
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "문의 목록 삭제 성공 (OK)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "문의 목록 삭제 실패 (INTERNAL_SERVER_ERROR)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 문의를 찾지 못함 (NOT_FOUND)"
            )
    })
    @DeleteMapping("/admin/inquiry/delete")
    public ResponseEntity<APIResponse<Integer>> deleteAdminInquiryList(
            @RequestBody @Valid InquiryAdminListDeleteRequestDto noticeListDeleteRequestDto);


    // 질문자 문의 등록 화면 조회 API
    @Operation(
            summary = "질문자 문의 등록 화면 조회",
            description = "질문자가 문의를 등록하기 위한 화면을 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "질문자 문의 등록 화면 조회에 성공 (OK)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "질문자 문의 등록 화면 조회에 실패 (NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            )
    })
    @GetMapping("/strategy/{strategyId}/inquiry")
    public ResponseEntity<APIResponse<InquirySavePageShowResponseDto>> showInquirySavePage (
            @RequestBody InquirySavePageShowRequestDto inquirySavePageShowRequestDto);


    // 질문자 문의 등록 API
    @Operation(
            summary = "질문자 문의 등록",
            description = "질문자가 문의를 등록하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)"
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "문의 등록에 성공 (OK)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "문의 등록에 실패 (INTERNAL_SERVER_ERROR)"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "데이터의 형식이 올바르지 않음 (BAD_REQUEST)"
            )
    })
    @PostMapping("/strategy/{strategyId}/inquiry")
    public ResponseEntity<APIResponse<Long>> saveInquirerInquiry(
            @RequestBody InquirySaveRequestDto inquirySaveRequestDto);


    // 질문자 문의 조회 / 검색 API
    @Operation(
            summary = "질문자 문의 조회 / 검색",
            description = "질문자가 자신의 문의를 조회하거나 검색하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "문의 데이터 조회에 성공 (OK)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "페이지 내에 한 개의 문의도 존재하지 않을 때 (NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            )
    })
    @Parameters({
            @Parameter(name = "sort", description = "정렬 순서 (사용: registrationDate, strategyName) (설명: '최신순', '전략명')"),
            @Parameter(name = "closed", description = "답변 상태 탭 (사용: all, closed, unclosed) (설명: 전체, 답변완료, 답변대기)")
    })
    @GetMapping("/member/inquiry")
    public ResponseEntity<APIResponse<PageResponse<InquiryListOneShowResponseDto>>> showInquirerInquiry (
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "closed") String closed);


    // 질문자 문의 상세 조회 API
    @Operation(
            summary = "질문자 문의 상세 조회",
            description = "질문자가 자신의 문의 상세 정보를 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "문의의 상세 데이터 조회에 성공 (OK)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "문의의 상세 데이터 조회에 실패 (NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            )
    })
    @GetMapping("/member/inquiry/{inquiryId}/view")
    public ResponseEntity<APIResponse<InquiryAnswerShowResponseDto>> showInquirerInquiryDetail (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "closed") String closed);


    // 질문자 문의 수정 화면 조회 API
    @Operation(
            summary = "질문자 문의 수정 화면 조회",
            description = "질문자가 자신의 문의를 수정하기 위한 화면을 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "질문자 문의 수정 화면 조회에 성공 (OK)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "질문자 문의 수정 화면 조회에 실패 (NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            )
    })
    @Parameters({
            @Parameter(name = "sort", description = "정렬 순서 (사용: registrationDate, strategyName) (설명: '최신순', '전략명')"),
            @Parameter(name = "closed", description = "답변 상태 탭 (사용: all, closed, unclosed) (설명: 전체, 답변완료, 답변대기)")
    })
    @GetMapping("/member/inquiry/{inquiryId}/modify")
    public ResponseEntity<APIResponse<InquiryModifyPageShowResponseDto>> showInquiryModifyPage (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "closed") String closed);


    // 질문자 문의 수정 API
    @Operation(
            summary = "질문자 문의 수정",
            description = "질문자가 자신의 문의를 수정하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)"
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "문의 수정에 성공 (OK)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "문의 수정에 실패 (INTERNAL_SERVER_ERROR)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 문의를 찾지 못했을 때 (NOT_FOUND)"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (BAD_REQUEST)"
            )
    })
    @PutMapping("/member/inquiry/{inquiryId}/modify")
    public ResponseEntity<APIResponse<Long>> modifyInquirerInquiry (
            @PathVariable Long inquiryId,
            @RequestBody @Valid InquiryModifyRequestDto inquiryModifyRequestDto);


    // 질문자 문의 삭제 API
    @Operation(
            summary = "질문자 문의 삭제",
            description = "질문자가 자신의 문의를 삭제하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)"
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "문의 삭제에 성공 (OK)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "문의 삭제에 실패 (INTERNAL_SERVER_ERROR)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 문의를 찾지 못했을 때 (NOT_FOUND)"
            )
    })
    @DeleteMapping("/member/inquiry/{inquiryId}/delete")
    public ResponseEntity<APIResponse<Long>> deleteInquirerInquiry (
            @PathVariable Long inquiryId);


    // 트레이더 문의 답변 등록 API
    @Operation(
            summary = "트레이더 문의 답변 등록",
            description = "트레이더 문의에 대한 답변을 등록하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)"
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "문의 답변이 등록에 성공했을 때 (OK)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "문의 답변 등록에 실패했을 때 (INTERNAL_SERVER_ERROR)"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "데이터의 형식이 올바르지 않음 (BAD_REQUEST)"
            )
    })
    @PostMapping("/trader/inquiry/{inquiryId}/write")
    public ResponseEntity<APIResponse<Long>> saveTraderInquiryAnswer (
            @PathVariable Long inquiryId,
            @RequestBody @Valid InquiryDetailSaveRequestDto inquiryDetailSaveRequestDto);


    // 트레이더 문의 조회 API
    @Operation(
            summary = "트레이더 문의 조회",
            description = "트레이더의 문의를 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "문의 데이터 조회에 성공했을 때 (OK)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "페이지 내에 한 개의 문의도 존재하지 않을 때 (NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            )
    })
    @Parameters({
            @Parameter(name = "sort", description = "정렬 순서 (사용: registrationDate, strategyName) (설명: '최신순', '전략명')"),
            @Parameter(name = "closed", description = "답변 상태 탭 (사용: all, closed, unclosed) (설명: 전체, 답변완료, 답변대기)")
    })
    @GetMapping("/trader/inquiry")
    public ResponseEntity<APIResponse<PageResponse<InquiryListOneShowResponseDto>>> showTraderInquiry (
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "closed") String closed);


    // 트레이더 문의 상세 조회 API
    @Operation(
            summary = "트레이더 문의 상세 조회",
            description = "트레이더 문의에 대한 상세 정보를 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "문의의 상세 데이터 조회에 성공했을 때 (OK)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "문의의 상세 데이터 조회에 실패했을 때 (NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = APIResponse.class))
            )
    })
    @Parameters({
            @Parameter(name = "sort", description = "정렬 순서 (사용: registrationDate, strategyName) (설명: '최신순', '전략명')"),
            @Parameter(name = "closed", description = "답변 상태 탭 (사용: all, closed, unclosed) (설명: 전체, 답변완료, 답변대기)")
    })
    @GetMapping("/trader/inquiry/{inquiryId}/view")
    public ResponseEntity<APIResponse<InquiryAnswerShowResponseDto>> showTraderInquiryDetail (
            @PathVariable Long inquiryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "closed") String closed);
}
