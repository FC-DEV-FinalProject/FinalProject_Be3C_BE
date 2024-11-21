package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.*;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class NoticeContoller implements NoticeControllerDocs {

    private final NoticeService noticeService;

    /*
        관리자 공지사항 등록 API
        1. 공지사항이 등록에 성공했을 때 : OK
        2. 공지사항이 등록에 실패했을 때 : INTERNAL_SERVER_ERROR
     */
    @Override
    @PostMapping("/admin/notice/write")
    public ResponseEntity<APIResponse<Long>> saveAdminNotice(
            @RequestBody NoticeSaveRequestDto noticeSaveRequestDto) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(noticeId));
    }

    /*
        관리자 공지사항 조회 / 검색 API
        1. 공지사항 데이터 조회에 성공했을 때 : OK
        2. 페이지 내에 한 개의 공지사항도 존재하지 않을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/admin/notice")
    public ResponseEntity<APIResponse<PageResponse<NoticeAdminShowResponseDto>>> showAdminNotice(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(adminNoticePage));
    }

    /*
        관리자 공지사항 상세 조회 API
        1. 공지사항의 상세 데이터 조회에 성공했을 때 : OK
        2. 공지사항의 상세 데이터 조회에 실패했을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/admin/notice/{noticeId}/view")
    public ResponseEntity<APIResponse<NoticeDetailAdminShowResponseDto>> showAdminNoticeDetail(
            @PathVariable Long noticeId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(showAdminNoticeDetailResponseDto));
    }

    /*
        관리자 공지사항 수정 화면 조회 API
        1. 공지사항 수정 화면 조회에 성공했을 때 : OK
        2. 공지사항 수정 화면 조회에 실패했을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/admin/notice/{noticeId}/modify")
    public ResponseEntity<APIResponse<NoticeDetailAdminShowResponseDto>> showModifyAdminNotice(
            @PathVariable Long noticeId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchText", required = false) String searchText) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(showAdminNoticeDetailResponseDto);
    }

    /*
        관리자 공지사항 수정 API
        1. 공지사항 수정에 성공했을 때 : OK
        2. 공지사항 수정에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 해당 공지사항을 찾지 못했을 때 : NOT_FOUND
     */
    @Override
    @PutMapping("/admin/notice/{noticeId}/modify")
    public ResponseEntity<APIResponse<Long>> modifyAdminNotice(
            @PathVariable Long noticeId,
            @RequestBody @Valid NoticeModifyRequestDto noticeModifyRequestDto) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(noticeModifyRequestDto.getNoticeId()));
    }

    /*
        관리자 공지사항 삭제 API
        1. 공지사항 삭제에 성공했을 때 : OK
        2. 공지사항 삭제에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 해당 공지사항을 찾지 못했을 때 : NOT_FOUND
     */
    @Override
    @DeleteMapping("/admin/notice/{noticeId}/delete")
    public ResponseEntity<APIResponse<Long>> deleteAdminNotice(
            @PathVariable Long noticeId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(noticeId));
    }

    /*
        관리자 공지사항 목록 삭제 API
        1. 공지사항 목록 삭제에 성공했을 때 : OK
        2. 공지사항 목록 삭제에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 해당 공지사항을 찾지 못했을 때 : NOT_FOUND
     */
    @Override
    @DeleteMapping("/admin/notice/delete")
    public ResponseEntity<APIResponse<Long>> deleteAdminNoticeList(
            @RequestBody @Valid NoticeListDeleteRequestDto noticeListDeleteRequestDto) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(deleteCount));
    }

    /*
        공지사항 조회 / 검색 API
        1. 공지사항 데이터 조회에 성공했을 때 : OK
        2. 페이지 내에 한 개의 공지사항도 존재하지 않을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/notice")
    public ResponseEntity<APIResponse<PageResponse<NoticeShowResponseDto>>> showNotice(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "searchText", required = false) String searchText) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(noticePage));
    }

    /*
        공지사항 상세 조회 API
        1. 공지사항의 상세 데이터 조회에 성공했을 때 : OK
        2. 공지사항의 상세 데이터 조회에 실패했을 때 : NOT_FOUND
     */
    @Override
    @GetMapping("/notice/{noticeId}/view")
    public ResponseEntity<APIResponse<NoticeDetailShowResponseDto>> showNoticeDetail(
            @PathVariable Long noticeId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "searchText", required = false) String searchText) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(showNoticeDetailResponseDto));
    }
}