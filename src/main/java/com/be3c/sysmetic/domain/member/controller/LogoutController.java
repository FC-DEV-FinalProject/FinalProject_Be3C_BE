package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.service.LogoutService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "로그아웃 API", description = "로그아웃")
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LogoutController {

    private final LogoutService logoutService;

    /*
        로그아웃 api
        1. 로그아웃 성공했을 때 : OK
     */
    @Operation(
            summary = "로그아웃",
            description = "사용자를 로그아웃 처리하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류로 인해 로그아웃 실패",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/auth/logout")
    public ResponseEntity<APIResponse<String>> logout(HttpServletRequest request) {
        logoutService.logout(request);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success("로그아웃이 성공적으로 처리되었습니다."));
    }

}
