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

import java.util.Map;
import java.util.NoSuchElementException;

@Controller
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InterestStrategyController {

    private final InterestStrategyService interestStrategyService;

    /*
        폴더 내 관심 전략을 페이지로 가져오는 api
        1. 해당 관심 전략 페이지를 찾는 데 성공했을 때 : OK
        2. 해당 페이지에 관심 전략이 존재하지 않을 때 : NOT_FOUND
        3. SecurityContext에 userId가 존재하지 않을 때 : FORBIDDEN
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
    /*
        관심 전략 등록 api
        1. 관심 전략 등록에 성공했을 때 : OK
        2. 관심 전략 등록에 실패했을 떄 : INTERNAL_SERVER_ERROR
        3. 이미 관심 전략에 등록된 전략일 때 : BAD_REQUEST
        4. 등록할 전략을 찾지 못했을 때 : NOT_FOUND
        5. SecurityContext에 userId가 존재하지 않을 떄 : FORBIDDEN
     */
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(ErrorCode.NOT_FOUND));
        }
    }

    /*
        관심 전략 선택 삭제 api (단일 삭제 포함)
        1. 선택한 관심 전략 전부 삭제에 성공했을 때 : OK
        2. 선택한 관심 전략 중 일부만 삭제에 성공했을 때 : MULTI_STATUS
        3. SecurityContext에 userId가 존재하지 않을 떄 : FORBIDDEN
     */
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
        } catch (AuthenticationCredentialsNotFoundException |
                 UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN));
        }
    }
}
