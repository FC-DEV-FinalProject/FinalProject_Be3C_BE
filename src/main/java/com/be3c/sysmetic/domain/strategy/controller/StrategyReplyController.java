package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.PageReplyResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyDeleteRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyGetPageRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.service.ReplyService;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StrategyReplyController {

    private final ReplyService replyService;

    // 나중에 mypageController로 이동해야함.
    @GetMapping("/mypage/reply/{page}")
    public ResponseEntity<ApiResponse<PageResponseDto<PageReplyResponseDto>>> getReplyPage(
            @PathVariable Integer page
    ) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(replyService.getMyReplyPage(page)));
        } catch (AuthenticationCredentialsNotFoundException |
                 UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN));
        }
    }

    @GetMapping("/strategy/reply")
    public ResponseEntity<ApiResponse<PageResponseDto<PageReplyResponseDto>>> getReplyPage(
            @RequestParam ReplyGetPageRequestDto replyGetPageRequestDto
    ) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(replyService.getReplyPage(replyGetPageRequestDto)));
        } catch (AuthenticationCredentialsNotFoundException |
                 UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN));
        }
    }

    @PostMapping("/strategy/reply")
    public ResponseEntity<ApiResponse<String>> postReply(
            @RequestBody ReplyPostRequestDto replyPostRequestDto
    ) throws Exception {
        try {
            if(replyService.insertReply(replyPostRequestDto)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST));
        } catch (AuthenticationCredentialsNotFoundException |
                UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN));
        }
    }

    // @PreAuthorize("hasRole='ROLE_USER and !ROLE_TRADER'")
    @PostMapping("/strategy/reply")
    public ResponseEntity<ApiResponse<String>> deleteReply(
            @RequestBody ReplyDeleteRequestDto replyDeleteRequestDto
    ) throws Exception {
        try {
            if(replyService.deleteReply(replyDeleteRequestDto)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST));
        } catch (AuthenticationCredentialsNotFoundException |
                 UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN));
        }
    }
}
