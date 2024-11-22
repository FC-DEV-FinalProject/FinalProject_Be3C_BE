package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.MemberPatchInfoRequestDto;
import com.be3c.sysmetic.domain.member.dto.MemberPutPasswordRequestDto;
import com.be3c.sysmetic.global.common.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface MemberInfoControllerDocs {

    /*
       회원 비밀번호 변경 api
       1. 비밀번호 변경에 성공했을 때 : OK
       2. 비밀번호 변경에 실패했을 때 : INTERNAL_SERVER_ERROR
       3. 해당 유저가 존재하지 않을 때 : NOT_FOUND
       4. Security Context에 userId가 존재하지 않을 때 : FORBIDDEN
    */
    @Operation(summary = "회원 비밀번호 변경", description = "회원 비밀번호를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "비밀번호 변경 실패 (서버 에러)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 유저가 존재하지 않음",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "SecurityContext에 userId가 존재하지 않음",
                    content = @Content)
    })
    @PutMapping("/member/info/password")
    public ResponseEntity<APIResponse<String>> putPassword(
            @RequestBody MemberPutPasswordRequestDto memberPutPasswordRequestDto,
            HttpServletRequest request
    );

    /*
        회원 정보를 업데이트하는 api
        1. 수정에 성공했을 때 : OK
        2. 수정에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 수정할 회원을 찾지 못했을 때 : NOT_FOUND
        4. Security Context에서 회원 Id를 찾지 못했을 때 : FORBIDDEN
     */
    @Operation(summary = "회원 정보 업데이트", description = "회원 정보를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "회원 정보 수정 실패 (서버 에러)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "수정할 회원을 찾을 수 없음",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "SecurityContext에서 회원 ID를 찾을 수 없음",
                    content = @Content)
    })
    @PatchMapping("/member/info")
    public ResponseEntity<APIResponse<String>> updateMemberInfo(
            @RequestBody MemberPatchInfoRequestDto memberPatchInfoRequestDto
    );

    /*
        회원 탈퇴 api
        1. 회원 탈퇴에 성공했을 때 : OK
        2. 회원 탈퇴에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 탈퇴할 회원을 찾지 못했을 때 : NOT_FOUND
        4. Security Context에서 회원 아이디를 찾지 못했을 때 : FORBIDDEN
     */
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "회원 탈퇴 실패 (서버 에러)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "탈퇴할 회원을 찾을 수 없음",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "SecurityContext에서 회원 ID를 찾을 수 없음",
                    content = @Content)
    })
    @DeleteMapping("/member/{id}")
    public ResponseEntity<APIResponse<String>> deleteMemberInfo(
            @PathVariable(name="id") Long userId,
            HttpServletRequest request
    );
}
