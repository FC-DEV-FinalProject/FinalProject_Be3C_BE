package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.MemberPatchInfoRequestDto;
import com.be3c.sysmetic.domain.member.dto.MemberPutPasswordRequestDto;
import com.be3c.sysmetic.domain.member.service.MemberInfoService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/api")
public class MemberInfoController {

    private final MemberInfoService memberInfoService;

    /*
        회원 비밀번호 변경 api
        1. 비밀번호 변경에 성공했을 때 : OK
        2. 비밀번호 변경에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 해당 유저가 존재하지 않을 때 : NOT_FOUND
        4. Security Context에 userId가 존재하지 않을 때 : FORBIDDEN
     */
    @PutMapping("/member/info/password")
    public ResponseEntity<APIResponse<String>> putPassword(
            @RequestBody MemberPutPasswordRequestDto memberPutPasswordRequestDto,
            HttpServletRequest request
    ) {
        try {
            if(memberInfoService
                    .changePassword(
                            memberPutPasswordRequestDto,
                            request)
            ) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST, e.getMessage()));
        }
    }

    /*
        회원 정보를 업데이트하는 api
        1. 수정에 성공했을 때 : OK
        2. 수정에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 수정할 회원을 찾지 못했을 때 : NOT_FOUND
        4. Security Context에서 회원 Id를 찾지 못했을 때 : FORBIDDEN
     */
    @PatchMapping("/member/info")
    public ResponseEntity<APIResponse<String>> updateMemberInfo(
            @RequestBody MemberPatchInfoRequestDto memberPatchInfoRequestDto
    ) {
        try {
            if(memberInfoService
                    .changeMemberInfo(
                            memberPatchInfoRequestDto
                    )) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }

    /*
        회원 탈퇴 api
        1. 회원 탈퇴에 성공했을 때 : OK
        2. 회원 탈퇴에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 탈퇴할 회원을 찾지 못했을 때 : NOT_FOUND
        4. Security Context에서 회원 아이디를 찾지 못했을 때 : FORBIDDEN
     */
    @DeleteMapping("/member/{id}")
    public ResponseEntity<APIResponse<String>> deleteMemberInfo(
            @PathVariable(name="id") Long userId,
            HttpServletRequest request
    ) {
        try {
            if(memberInfoService.deleteUser(userId, request)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }
}
