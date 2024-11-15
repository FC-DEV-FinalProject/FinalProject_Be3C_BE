package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.InterestStrategyGetRequestDto;
import com.be3c.sysmetic.domain.member.dto.InterestStrategyGetResponseDto;
import com.be3c.sysmetic.domain.member.dto.FollowDeleteRequestDto;
import com.be3c.sysmetic.domain.member.dto.FollowPostRequestDto;
import com.be3c.sysmetic.domain.member.service.InterestStrategyService;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.common.response.SuccessCode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.NoSuchElementException;

@Controller
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InterestStrategyController {

    private final InterestStrategyService interestStrategyService;

    /*
        폴더 내 관심 전략을 페이지로 가져오는 api
        이게 여기 있는 게 맞나?
     */
    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    @GetMapping("/member/interestStrategy")
    public ResponseEntity<ApiResponse<PageResponse<InterestStrategyGetResponseDto>>> getFolderPage(
            @ModelAttribute InterestStrategyGetRequestDto interestStrategyGetRequestDto
    ) throws Exception {
        try {
            PageResponse<InterestStrategyGetResponseDto> interestStrategyPage =
                    interestStrategyService.getInterestStrategyPage(
                            interestStrategyGetRequestDto
                    );

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(interestStrategyPage));
        } catch (AuthenticationCredentialsNotFoundException |
                 UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN));
        } catch (EntityNotFoundException |
                 NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(ErrorCode.NOT_FOUND));
        }
    }

    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    @PostMapping("/strategy/follow")
    public ResponseEntity<ApiResponse<String>> follow(
        @Valid @RequestBody FollowPostRequestDto followPostRequestDto
    ) throws Exception {
        try {
            if(interestStrategyService.follow(followPostRequestDto)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        } catch (AuthenticationCredentialsNotFoundException |
                 UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN));
        } catch (IllegalArgumentException |
                 EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST));
        }
    }

    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    @DeleteMapping("/strategy/follow")
    public ResponseEntity<ApiResponse<Map<Long, String>>> unfollow(
            @Valid @RequestBody FollowDeleteRequestDto followPostRequestDto
    ) throws Exception {
        try {
            Map<Long, String> unFollowResult = interestStrategyService.unfollow(
                    followPostRequestDto
            );

            if(unFollowResult.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }

            return ResponseEntity.status(HttpStatus.MULTI_STATUS)
                    .body(ApiResponse.success(SuccessCode.OK, unFollowResult));
        } catch (AuthenticationCredentialsNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN, e.getMessage()));
        } catch (IllegalArgumentException |
                 EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, e.getMessage()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, e.getMessage()));
        }
    }
}
