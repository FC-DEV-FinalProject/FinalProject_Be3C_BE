package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.FindEmailRequestDto;
import com.be3c.sysmetic.domain.member.dto.ResetPasswordRequestDto;
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

@Tag(name = "이메일 찾기 및 비밀번호 재설정 API", description = "이메일 찾기 및 비밀번호 재설정")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class AccountController {

    private final AccountService accountService;
    private final RegisterService registerService;

    // 이메일 찾기 api
    @Operation(
            summary = "이메일 찾기",
            description = "사용자가 입력한 정보를 기반으로 이메일을 찾는 API"
    )
    @PostMapping("/auth/find-email")
    public ResponseEntity<APIResponse<String>> findEmail(@Valid @RequestBody FindEmailRequestDto findEmailRequestDto,
                                                         HttpServletRequest request) {
        String result = accountService.findEmail(findEmailRequestDto.getName(), findEmailRequestDto.getPhoneNumber());
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(result));
        // todo: 2차 개발 고려(request는 FindEmailLog 구현 시 사용될 예정)
    }

    // 이메일 확인 및 인증코드 발송 api
    @Operation(
            summary = "이메일 확인 및 인증 코드 발송",
            description = "사용자가 입력한 이메일의 유효성을 확인하고 인증 코드를 발송하는 API"
    )
    @GetMapping("/auth/reset-password")
    public ResponseEntity<APIResponse<String>> checkEmailAndSendCode(@Email(message = "{Invalid.email}") @RequestParam String email) {
        accountService.isPresentEmail(email);
        registerService.sendVerifyEmailCode(email);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success());
    }

    // 비밀번호 재설정 api
    @Operation(
            summary = "비밀번호 재설정",
            description = "사용자가 새로운 비밀번호를 설정하는 API"
    )
    @PostMapping("/auth/reset-password")
    public ResponseEntity<APIResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDto resetPasswordRequestDtoRequestDto,
                                                             HttpServletRequest request) {
        accountService.isPasswordMatch(resetPasswordRequestDtoRequestDto.getPassword(), resetPasswordRequestDtoRequestDto.getRewritePassword());
        accountService.resetPassword(resetPasswordRequestDtoRequestDto.getEmail(), resetPasswordRequestDtoRequestDto.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success());
        // todo: 2차 개발 고려(request는 ResetPasswordLg 구현 시 사용될 예정)
    }

}
