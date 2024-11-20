package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.FindEmailRequestDto;
import com.be3c.sysmetic.domain.member.service.AccountService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/auth/find-email")
    public ResponseEntity<APIResponse<String>> findEmail(@Valid @RequestBody FindEmailRequestDto findEmailRequestDto, HttpServletRequest request) {
        String result = accountService.findEmail(findEmailRequestDto.getName(), findEmailRequestDto.getPhoneNumber());
        if(result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.fail(ErrorCode.BAD_REQUEST, "일치하는 회원 정보를 찾을 수 없습니다."));
        }
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(result));
        // todo: 2차 개발 고려(request는 FindEmailLog 구현 시 사용될 예정)
    }

}
