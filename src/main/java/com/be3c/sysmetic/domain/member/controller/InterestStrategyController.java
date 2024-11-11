package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.FolderGetRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderGetResponseDto;
import com.be3c.sysmetic.domain.member.dto.FollowPostRequestDto;
import com.be3c.sysmetic.domain.member.service.InterestStrategyService;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.util.CustomUserDetails;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InterestStrategyController {

    private final InterestStrategyService interestStrategyService;

    private final SecurityUtils securityUtils;

    /*
        폴더 내 관심 전략을 페이지로 가져오는 api
        이게 여기 있는 게 맞나?
     */
    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    @GetMapping("/member/interestStrategy")
    public ResponseEntity<ApiResponse<Page<FolderGetResponseDto>>> getFolderPage(
            @RequestParam FolderGetRequestDto folderGetRequestDto
    ) throws Exception {
        try {
            Page<FolderGetResponseDto> interestStrategyPage =
                    interestStrategyService
                            .getInterestStrategyPage(
                                    folderGetRequestDto,
                                    securityUtils.getUserIdInSecurityContext()
                            );

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(interestStrategyPage));
        } catch (AuthenticationCredentialsNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN, e.getMessage()));
        }
    }

    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    @PostMapping("/strategy/follow")
    public ResponseEntity<ApiResponse<String>> follow(
        @RequestBody FollowPostRequestDto followPostRequestDto
    ) throws Exception {
        try {
            if(interestStrategyService.follow(
                    followPostRequestDto,
                    securityUtils.getUserIdInSecurityContext()
            )) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST));
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

    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")

}
