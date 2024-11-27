package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.MemberGetResponseDto;
import com.be3c.sysmetic.domain.member.dto.MemberManagementPatchRequestDto;
import com.be3c.sysmetic.domain.member.entity.MemberSearchRole;
import com.be3c.sysmetic.domain.member.entity.MemberSearchType;
import com.be3c.sysmetic.domain.member.service.MemberManagementService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "회원관리 API", description = "관리자의 회원관리")
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/v1")
public class MemberManagementController {

    private final MemberManagementService memberManagementService;

    // 관리자 회원 조회 api
    @Operation(
            summary = "관리자 - 회원 조회",
            description = "관리자가 특정 역할(role)과 검색 조건을 기반으로 회원 목록을 조회하는 API"
    )
    @GetMapping("/admin/members")
    public ResponseEntity<APIResponse<PageResponse<MemberGetResponseDto>>> getMemberPage(@RequestParam MemberSearchRole role, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "all", required = false) MemberSearchType searchType, @RequestParam(required = false) String searchKeyword) {
        /*
            조회 대상) all, user, trader, manager
            검색 필터) all, email, name, nickname, phoneNumber, birth
        */
        PageResponse<MemberGetResponseDto> members = memberManagementService.findMemberPage(role, page, searchType, searchKeyword);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(members));
    }

    // 관리자 회원 등급변경 api
    @Operation(
            summary = "관리자 - 회원 등급변경",
            description = "관리자가 특정 회원의 등급을 변경하는 API"
    )
    @PatchMapping("/admin/members")
    public ResponseEntity<APIResponse<String>> changeMemberRoleCode(@Valid @RequestBody MemberManagementPatchRequestDto responseDto) {
        for(Long member : responseDto.getMemberId()) {
            memberManagementService.changeRoleCode(member, responseDto.getHasManagerRights());
        }
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success());
    }

    // 관리자 회원 강제탈퇴 api
    @Operation(
            summary = "관리자 - 회원 강제탈퇴",
            description = "관리자가 특정 회원을 강제 탈퇴시키는 API"
    )
    @DeleteMapping("/admin/members")
    public ResponseEntity<APIResponse<String>> banMember(@RequestBody List<Long> memberId) {
        // 승열님 서비스 메서드 코드 추가 필요
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success());
    }


}
