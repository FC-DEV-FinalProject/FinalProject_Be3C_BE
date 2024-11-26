package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.FindEmailRequestDto;
import com.be3c.sysmetic.domain.member.dto.ResetPasswordRequestDto;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.service.AccountService;
import com.be3c.sysmetic.domain.member.service.RegisterService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "이메일 찾기 및 비밀번호 재설정 API", description = "이메일 찾기 및 비밀번호 재설정")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class AccountController {

    private final AccountService accountService;
    private final RegisterService registerService;

    /*
        이메일 찾기 api
        1. 이메일 찾기 성공했을 때 : OK
        2. 일치하는 회원 정보를 찾을 수 없을 때 : BAD_REQUEST
        3. 이메일 찾기 실패했을 때 : INTERNAL_SERVER_ERROR
     */
    @Operation(
            summary = "이메일 찾기",
            description = "사용자가 입력한 정보를 기반으로 이메일을 찾는 API"
    )
    @PostMapping("/auth/find-email")
    public ResponseEntity<APIResponse<String>> findEmail(@Valid @RequestBody FindEmailRequestDto findEmailRequestDto, HttpServletRequest request) {
        String result = accountService.findEmail(findEmailRequestDto.getName(), findEmailRequestDto.getPhoneNumber());
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(result));
        // todo: 2차 개발 고려(request는 FindEmailLog 구현 시 사용될 예정)
    }

    /*
        이메일 확인 및 인증코드 발송 api
        1. 인증코드 발송 성공했을 때 : OK
        2. 이메일 확인에 실패했을 때 : BAD_REQUEST
        3. 이메일 전송에 실패했을 때: INTERNAL_SERVER_ERROR
     */
    @Operation(
            summary = "이메일 확인 및 인증 코드 발송",
            description = "사용자가 입력한 이메일의 유효성을 확인하고 인증 코드를 발송하는 API"
    )
    @GetMapping("/auth/reset-password")
    public ResponseEntity<APIResponse<String>> checkEmailAndSendCode(@Email(message = "유효한 이메일 형식이 아닙니다.") @RequestParam String email) {
        if(!accountService.isPresentEmail(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.fail(ErrorCode.BAD_REQUEST));
        }
        if(!registerService.sendVerifyEmailCode(email)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        }
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success());
    }


    /*
        비밀번호 재설정 api
        1. 비밀번호 재설정 성공했을 때 : OK
        2. 비밀번호가 불일치할 때 : BAD_REQUEST
        3. 비밀번호 재설정 실패했을 때 : INTERNAL_SERVER_ERROR
     */
    @Operation(
            summary = "비밀번호 재설정",
            description = "사용자가 새로운 비밀번호를 설정하는 API"
    )
    @PostMapping("/auth/reset-password")
    public ResponseEntity<APIResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDto requestDto, HttpServletRequest request) {
        if(!accountService.isPasswordMatch(requestDto.getPassword(), requestDto.getRewritePassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.fail(ErrorCode.BAD_REQUEST, "비밀번호 불일치"));
        }
        if(!accountService.resetPassword(requestDto.getEmail(), requestDto.getPassword())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        }
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success());
        // todo: 2차 개발 고려(request는 ResetPasswordLg 구현 시 사용될 예정)
    }

}
