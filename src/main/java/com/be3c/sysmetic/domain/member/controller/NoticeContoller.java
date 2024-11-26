package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.*;
import com.be3c.sysmetic.domain.member.service.NoticeService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class NoticeContoller implements NoticeControllerDocs {

    private final NoticeService noticeService;

    /*
        관리자 공지사항 등록 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공지사항이 등록에 성공했을 때 : OK
        3. 공지사항이 등록에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 데이터의 형식이 올바르지 않음 : BAD_REQUEST
     */
    @Override
    @PostMapping("/v1/admin/notice/write")
    public ResponseEntity<APIResponse<Long>> saveAdminNotice(
            @RequestBody NoticeSaveRequestDto noticeSaveRequestDto) {

        Long noticeId = 1L;
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(noticeId));
    }

    /*
        관리자 공지사항 조회 / 검색 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공지사항 데이터 조회에 성공했을 때 : OK
        3. 페이지 내에 한 개의 공지사항도 존재하지 않을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/v1/admin/notice")
    public ResponseEntity<APIResponse<PageResponse<NoticeAdminListOneShowResponseDto>>> showAdminNotice(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {

        List<NoticeAdminListOneShowResponseDto> noticeAdminList = new ArrayList<>();
        PageResponse<NoticeAdminListOneShowResponseDto> adminNoticePage = PageResponse.<NoticeAdminListOneShowResponseDto>builder()
                .currentPage(1)
                .pageSize(10)
                .totalElement(100)
                .totalPages(10)
                .content(noticeAdminList)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(adminNoticePage));
    }

    /*
        관리자 공지사항 목록 공개여부 수정 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공개여부 수정에 성공했을 때 : OK
        3. 공개여부 수정에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 해당 공지사항을 찾지 못했을 때 : NOT_FOUND
     */
    @Override
    @PutMapping("/v1/admin/notice/{noticeId}/closed")
    public ResponseEntity<APIResponse<Long>> modifyNoticeClosed() {

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    /*
        관리자 공지사항 상세 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공지사항의 상세 데이터 조회에 성공했을 때 : OK
        3. 공지사항의 상세 데이터 조회에 실패했을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/v1/admin/notice/{noticeId}/view")
    public ResponseEntity<APIResponse<NoticeDetailAdminShowResponseDto>> showAdminNoticeDetail(
            @PathVariable Long noticeId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {

        NoticeDetailAdminShowResponseDto noticeDetailAdminShowResponseDto = new NoticeDetailAdminShowResponseDto();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(noticeDetailAdminShowResponseDto));
    }

    /*
        관리자 공지사항 수정 화면 조회 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공지사항 수정 화면 조회에 성공했을 때 : OK
        3. 공지사항 수정 화면 조회에 실패했을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/v1/admin/notice/{noticeId}/modify")
    public ResponseEntity<APIResponse<NoticeShowModifyPageResponseDto>> showModifyAdminNotice(
            @PathVariable Long noticeId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {

        NoticeShowModifyPageResponseDto noticeShowModifyPageResponseDto = new NoticeShowModifyPageResponseDto();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(noticeShowModifyPageResponseDto));
    }

    /*
        관리자 공지사항 수정 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공지사항 수정에 성공했을 때 : OK
        3. 공지사항 수정에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 해당 공지사항을 찾지 못했을 때 : NOT_FOUND
        5. 데이터의 형식이 올바르지 않음 : BAD_REQUEST
            +) 공지사항 수정 화면에 들어온 시간이 해당 공지사항 최종수정일시보다 작음
     */
    @Override
    @PutMapping("/v1/admin/notice/{noticeId}/modify")
    public ResponseEntity<APIResponse<Long>> modifyAdminNotice(
            @PathVariable Long noticeId,
            @RequestBody @Valid NoticeModifyRequestDto noticeModifyRequestDto) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    /*
        관리자 공지사항 삭제 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공지사항 삭제에 성공했을 때 : OK
        3. 공지사항 삭제에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 해당 공지사항을 찾지 못했을 때 : NOT_FOUND
     */
    @Override
    @DeleteMapping("/v1/admin/notice/{noticeId}/delete")
    public ResponseEntity<APIResponse<Long>> deleteAdminNotice(
            @PathVariable Long noticeId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    /*
        관리자 공지사항 목록 삭제 API
        1. 사용자 인증 정보가 없음 : FORBIDDEN
        2. 공지사항 목록 삭제에 성공했을 때 : OK
        3. 공지사항 목록 삭제에 실패했을 때 : INTERNAL_SERVER_ERROR
        4. 해당 공지사항을 찾지 못했을 때 : NOT_FOUND
        5. 공지사항 중 일부만 삭제에 실패했을 때 : MULTI_STATUS
     */
    @Override
    @DeleteMapping("/v1/admin/notice/delete")
    public ResponseEntity<APIResponse<Integer>> deleteAdminNoticeList(
            @RequestBody @Valid NoticeListDeleteRequestDto noticeListDeleteRequestDto) {

        Integer deleteCount = 1;

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(deleteCount));
    }

    /*
        공지사항 조회 / 검색 API
        1. 공지사항 데이터 조회에 성공했을 때 : OK
        2. 페이지 내에 한 개의 공지사항도 존재하지 않을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/v1/notice")
    public ResponseEntity<APIResponse<PageResponse<NoticeListOneShowResponseDto>>> showNotice(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "searchText", required = false) String searchText) {

        List<NoticeListOneShowResponseDto> noticeList = new ArrayList<>();
        PageResponse<NoticeListOneShowResponseDto> noticePage = PageResponse.<NoticeListOneShowResponseDto>builder()
                .currentPage(1)
                .pageSize(10)
                .totalElement(100)
                .totalPages(10)
                .content(noticeList)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(noticePage));
    }

    /*
        공지사항 상세 조회 API
        1. 공지사항의 상세 데이터 조회에 성공했을 때 : OK
        2. 공지사항의 상세 데이터 조회에 실패했을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/v1/notice/{noticeId}/view")
    public ResponseEntity<APIResponse<NoticeDetailShowResponseDto>> showNoticeDetail(
            @PathVariable Long noticeId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "searchText", required = false) String searchText) {

        NoticeDetailShowResponseDto noticeDetailShowResponseDto = new NoticeDetailShowResponseDto();

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(noticeDetailShowResponseDto));
    }
}