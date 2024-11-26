package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.EmailResponseDto;
import com.be3c.sysmetic.domain.member.dto.RegisterRequestDto;
import com.be3c.sysmetic.domain.member.service.RegisterService;
import com.be3c.sysmetic.domain.member.validation.RegisterValidator;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원가입 API", description = "회원가입")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class RegisterController {

    private final RegisterService registerService;
    private final RegisterValidator registerValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        Object target = binder.getTarget();
        if(target instanceof RegisterRequestDto) {
            log.info("init binder {}", binder.toString());
            binder.addValidators(registerValidator);    // 커스텀 Validator 등록
        }
    }

    /*
        이메일 중복확인 api
        1. 이메일 중복확인 성공했을 때 : OK
        2. 중복된 이메일일 때 : CONFLICT
     */
    @Operation(
            summary = "이메일 중복확인",
            description = "이메일 중복 여부를 확인하는 API"
    )
    @GetMapping("/auth/check-duplicate-email")
    public ResponseEntity<APIResponse<String>> checkDuplicateEmail(@Email(message = "유효한 이메일 형식이 아닙니다.") @RequestParam String email) {
        if(registerService.checkEmailDuplication(email)) {
            // 중복O
            return ResponseEntity.status(HttpStatus.CONFLICT).body(APIResponse.fail(ErrorCode.DUPLICATE_RESOURCE, "이미 사용 중인 이메일입니다. 다른 이메일을 입력해 주세요."));
        }
        // 중복X
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success());
    }

    /*
        이메일 인증코드 전송 api
        1. 인증코드 전송 성공했을 때 : OK
     */
    @Operation(
            summary = "이메일 인증코드 전송",
            description = "사용자에게 이메일 인증코드를 전송하는 API"
    )
    @GetMapping("/auth/email-code")
    public ResponseEntity<APIResponse<String>> sendVerificationCode(@Email(message = "유효한 이메일 형식이 아닙니다.") @RequestParam String email) {
        registerService.sendVerifyEmailCode(email);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success());
    }

    /*
        이메일 인증코드 확인 api
        1. 인증코드 확인 성공했을 때 : OK
        2. 인증코드가 불일치할 때 : BAD_REQUEST
     */
    @Operation(
            summary = "이메일 인증코드 확인",
            description = "사용자가 입력한 이메일 인증코드를 검증하는 API"
    )
    @PostMapping("/auth/email-code")
    public ResponseEntity<APIResponse<String>> verifyCode(@Valid @RequestBody EmailResponseDto emailResponseDto) {
        if (registerService.checkVerifyEmailCode(emailResponseDto.getEmail(), emailResponseDto.getAuthCode())) {
            // 인증 성공
            return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.fail(ErrorCode.BAD_REQUEST, "인증코드가 일치하지 않습니다. 올바른 인증코드로 입력해 주세요."));
    }

    /*
        닉네임 중복확인 api
        1. 닉네임 중복확인 성공했을 때 : OK
        2. 중복된 닉네임일 때 : CONFLICT
     */
    @Operation(
            summary = "닉네임 중복확인",
            description = "닉네임 중복 여부를 확인하는 API"
    )
    @GetMapping("/auth/check-nickname")
    public ResponseEntity<APIResponse<String>> checkDuplicateNickname(@NotNull @Pattern(regexp = "^[가-힣0-9]{3,10}$", message = "닉네임은 한글 또는 숫자로 3자 이상 10자 이내로 입력해야 합니다.") @RequestParam String nickname) {
        if(registerService.checkNicknameDuplication(nickname)) {
            // 중복O
            return  ResponseEntity.status(HttpStatus.CONFLICT).body(APIResponse.fail(ErrorCode.DUPLICATE_RESOURCE, "이미 사용 중인 닉네임입니다. 다른 닉네임을 입력해 주세요."));
        }
        // 중복X
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success());
    }

    /*
        회원가입 api
        1. 회원가입 성공했을 때 : OK
        2. 회원가입 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 검증에 실패했을 때 : BAD_REQUEST
     */
    @Operation(
            summary = "회원가입",
            description = "사용자 회원가입을 처리하는 API"
    )
    @PostMapping("/auth/register")
    public ResponseEntity<APIResponse<String>> register(@Valid @RequestBody RegisterRequestDto registerResponseDto, Errors errors) {
        // Valid 에러 처리
        if(!errors.getAllErrors().isEmpty()) {
            String errorMessage = errors.getAllErrors().stream().findFirst().get().getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.fail(ErrorCode.BAD_REQUEST, errorMessage));
        }

        try {
            registerService.registerMember(registerResponseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR, "회원가입에 실패했습니다. 관리자에게 문의해 주세요."));
        }
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success());
    }
}
