package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.global.common.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "토큰 유효성 확인 API", description = "로그인 상태 확인용")
@RestController
@RequestMapping("/v1")
public class TokenController {

    @Operation(
            summary = "토큰 유효성 확인 API",
            description = "로그인 상태 확인용"
    )
    @GetMapping("/auth")
    public ResponseEntity<APIResponse> checkToken() {
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success());
    }

}
