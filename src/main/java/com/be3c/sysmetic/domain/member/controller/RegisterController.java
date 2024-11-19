package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.EmailResponseDto;
import com.be3c.sysmetic.domain.member.dto.RegisterResponseDto;
import com.be3c.sysmetic.domain.member.service.RegisterService;
import com.be3c.sysmetic.domain.member.validation.RegisterValidator;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;
    private final RegisterValidator registerValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        Object target = binder.getTarget();
        if(target instanceof RegisterResponseDto) {
            log.info("init binder {}", binder.toString());
            binder.addValidators(registerValidator);    // 커스텀 Validator 등록
        }
    }


    // 1. 이메일 중복확인
    @GetMapping("/auth/check-duplicate-email")
    public ResponseEntity<ApiResponse<String>> checkDuplicateEmail(@Email(message = "유효한 이메일 형식이 아닙니다.") @RequestParam String email) {
        if(registerService.checkEmailDuplication(email)) {
            // 중복O
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "이미 사용 중인 이메일입니다. 다른 이메일을 입력해 주세요."));
        }
        // fixme
        // 중복X
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }

    // 2. 이메일 인증코드 전송
    @PostMapping("/auth/send-verification-code")
    public ResponseEntity<ApiResponse<String>> sendVerificationCode(@Email(message = "유효한 이메일 형식이 아닙니다.") @RequestParam String email) {
        // 이메일 인증코드 발송 내용 추가되면 수정.
        String authCode = "ABC";

        registerService.sendVerifyEmailCode(email);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }

    // 3. 이메일 인증코드 확인
    @PostMapping("/auth/verify-code")
    public ResponseEntity<ApiResponse<String>> verifyCode(@Valid @RequestBody EmailResponseDto emailResponseDto) {
        if (registerService.checkVerifyEmailCode(emailResponseDto.getEmail(), emailResponseDto.getAuthCode())) {
            // 인증 성공
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "인증코드가 일치하지 않습니다. 올바른 인증코드로 입력해 주세요."));
    }

    // 닉네임 중복확인
    @GetMapping("/auth/check-nickname")
    public ResponseEntity<ApiResponse<String>> checkDuplicateNickname(@NotNull @Pattern(regexp = "^[가-힣0-9]{3,10}$", message = "닉네임은 한글 또는 숫자로 3자 이상 10자 이내로 입력해야 합니다.") @RequestParam String nickname) {
        if(registerService.checkNicknameDuplication(nickname)) {
            // 중복O
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "이미 사용 중인 닉네임입니다. 다른 닉네임을 입력해 주세요."));
        }
        // 중복X
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }

    // 회원가입
    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterResponseDto registerResponseDto, Errors errors) {
        // Valid 에러 처리
        if(!errors.getAllErrors().isEmpty()) {
            String errorMessage = errors.getAllErrors().stream().findFirst().get().getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(ErrorCode.BAD_REQUEST, errorMessage));
        }

        try {
            registerService.registerMember(registerResponseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "회원가입에 실패했습니다. 관리자에게 문의해 주세요."));
        }
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }
}
