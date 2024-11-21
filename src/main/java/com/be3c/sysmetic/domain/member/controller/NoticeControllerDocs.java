package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.*;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "공지사항 API", description = "관리자, 트레이더, 투자자 공지사항 API")
public interface NoticeControllerDocs {

    // 관리자 공지사항 등록 API
    @Operation(
            summary = "관리자 공지사항 등록",
            description = "관리자가 새로운 공지사항을 등록하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "공지사항 등록 성공 (OK)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "공지사항 등록 실패 (INTERNAL_SERVER_ERROR)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "데이터의 형식이 올바르지 않음 (BAD_REQUEST)",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/admin/notice/write")
    public ResponseEntity<APIResponse<Long>> saveAdminNotice(
            @RequestBody NoticeSaveRequestDto noticeSaveRequestDto);


    // 관리자 공지사항 조회 / 검색 API
    @Operation(
            summary = "관리자 공지사항 조회/검색",
            description = "관리자가 공지사항을 조회하거나 검색하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "공지사항 데이터 조회 성공 (OK)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "페이지 내 공지사항이 없음 (NOT_FOUND)",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/admin/notice")
    public ResponseEntity<APIResponse<PageResponse<NoticeAdminShowResponseDto>>> showAdminNotice(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText);


    // 관리자 공지사항 상세 조회 API
    @Operation(
            summary = "관리자 공지사항 상세 조회",
            description = "관리자가 특정 공지사항의 상세 데이터를 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "공지사항 상세 데이터 조회 성공 (OK)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "공지사항 상세 데이터 조회 실패 (NOT_FOUND)",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/admin/notice/{noticeId}/view")
    public ResponseEntity<APIResponse<NoticeDetailAdminShowResponseDto>> showAdminNoticeDetail(
            @PathVariable Long noticeId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText);


    // 관리자 공지사항 수정 화면 조회 API
    @Operation(
            summary = "관리자 공지사항 수정 화면 조회",
            description = "관리자가 특정 공지사항 수정 화면 데이터를 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "공지사항 수정 화면 데이터 조회 성공 (OK)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "공지사항 수정 화면 데이터 조회 실패 (NOT_FOUND)",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/admin/notice/{noticeId}/modify")
    public ResponseEntity<APIResponse<NoticeDetailAdminShowResponseDto>> showModifyAdminNotice(
            @PathVariable Long noticeId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText);


    // 관리자 공지사항 수정 API
    @Operation(
            summary = "관리자 공지사항 수정",
            description = "관리자가 기존 공지사항을 수정하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "공지사항 수정 성공 (OK)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "공지사항 수정 실패 (INTERNAL_SERVER_ERROR)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 공지사항을 찾지 못함 (NOT_FOUND)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "데이터의 형식이 올바르지 않음 (BAD_REQUEST)",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PutMapping("/admin/notice/{noticeId}/modify")
    public ResponseEntity<APIResponse<Long>> modifyAdminNotice(
            @PathVariable Long noticeId,
            @RequestBody @Valid NoticeModifyRequestDto noticeModifyRequestDto);


    // 관리자 공지사항 삭제 API
    @Operation(
            summary = "관리자 공지사항 삭제",
            description = "관리자가 특정 공지사항을 삭제하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "공지사항 삭제 성공 (OK)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "공지사항 삭제 실패 (INTERNAL_SERVER_ERROR)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 공지사항을 찾지 못함 (NOT_FOUND)",
                    content = @Content(mediaType = "application/json")
            )
    })
    @DeleteMapping("/admin/notice/{noticeId}/delete")
    public ResponseEntity<APIResponse<Long>> deleteAdminNotice(
            @PathVariable Long noticeId);


    // 관리자 공지사항 목록 삭제 API
    @Operation(
            summary = "관리자 공지사항 목록 삭제",
            description = "관리자가 목록 화면에서 여러 공지사항을 한 번에 삭제하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "공지사항 목록 삭제 성공 (OK)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "공지사항 목록 삭제 실패 (INTERNAL_SERVER_ERROR)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 공지사항을 찾지 못함 (NOT_FOUND)",
                    content = @Content(mediaType = "application/json")
            )
    })
    @DeleteMapping("/admin/notice/delete")
    public ResponseEntity<APIResponse<Long>> deleteAdminNoticeList(
            @RequestBody @Valid NoticeListDeleteRequestDto noticeListDeleteRequestDto);

    // 공지사항 조회 / 검색 API
    @Operation(
            summary = "공지사항 조회 및 검색",
            description = "공지사항을 조회하거나 검색하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "공지사항 조회 성공 (OK)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "공지사항 없음 (NOT_FOUND)",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/notice")
    public ResponseEntity<APIResponse<PageResponse<NoticeShowResponseDto>>> showNotice(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "searchText", required = false) String searchText);


    // 공지사항 상세 조회 API
    @Operation(
            summary = "공지사항 상세 조회",
            description = "공지사항의 상세 데이터를 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "공지사항 상세 조회 성공 (OK)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "공지사항 상세 조회 실패 (NOT_FOUND)",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/notice/{noticeId}/view")
    public ResponseEntity<APIResponse<NoticeDetailShowResponseDto>> showNoticeDetail(
            @PathVariable Long noticeId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "searchText", required = false) String searchText);
}
