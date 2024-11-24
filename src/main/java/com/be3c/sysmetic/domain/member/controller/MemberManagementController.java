package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.MemberGetResponseDto;
import com.be3c.sysmetic.domain.member.service.MemberManagementService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Tag(name = "회원관리 API", description = "관리자의 회원관리")
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MemberManagementController {

    private final MemberManagementService memberManagementService;

    /*
        관리자 회원 조회 api
        1. 회원 조회 성공했을 때 : OK
        2. 회원 조회 실패했을 때 : INTERNAL_SERVER_ERROR
        2. SecurityContext에 userId가 존재하지 않을 떄 : FORBIDDEN
    */
    @Operation(
            summary = "관리자 회원 조회",
            description = "관리자가 특정 역할(role)과 검색 조건을 기반으로 회원 목록을 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 조회 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "SecurityContext에 userId가 존재하지 않음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류로 인해 회원 조회 실패",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/admin/members")
    public ResponseEntity<APIResponse<PageResponse<MemberGetResponseDto>>> getMemberPage(@RequestParam String role, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "all", required = false) String searchType, @RequestParam(required = false) String searchKeyword) {
        /*
            조회 대상) all, user, trader, manager
            검색 필터) all, email, name, nickname, phoneNumber, birth
        */
        PageResponse<MemberGetResponseDto> members = memberManagementService.findMemberPage(role, page, searchType, searchKeyword);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(members));
    }

    /*
        관리자 회원 등급변경 api
        1. 등급변경 성공했을 때 : OK
        2. 등급변경 실패했을 때 : INTERNAL_SERVER_ERROR
        2. SecurityContext에 userId가 존재하지 않을 떄 : FORBIDDEN
    */
    @Operation(
            summary = "관리자 회원 등급 변경",
            description = "관리자가 특정 회원의 등급을 변경하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 등급 변경 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "SecurityContext에 userId가 존재하지 않음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류로 인해 등급 변경 실패",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PatchMapping("/admin/members")
    public ResponseEntity<APIResponse<String>> changeMemberRoleCode(@RequestParam Long memberId, @RequestParam boolean changeRoleCode) {
        memberManagementService.changeRoleCode(memberId, changeRoleCode);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success());
    }

    /*
        관리자 회원 강제탈퇴 api
        1. 강제탈퇴 성공했을 때 : OK
        2. 강제탈퇴 실패했을 때 : INTERNAL_SERVER_ERROR
        2. SecurityContext에 userId가 존재하지 않을 떄 : FORBIDDEN
    */
    @Operation(
            summary = "관리자 회원 강제 탈퇴",
            description = "관리자가 특정 회원을 강제 탈퇴시키는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 강제 탈퇴 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "SecurityContext에 userId가 존재하지 않음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류로 인해 강제 탈퇴 실패",
                    content = @Content(mediaType = "application/json")
            )
    })
    @DeleteMapping("/admin/members")
    public ResponseEntity<APIResponse<String>> banMember(@RequestParam Long memberId) {
        // 승열님 서비스 메서드 코드 추가 필요
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success());
    }


}
