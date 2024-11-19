package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyApprovalGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.RejectStrategyApprovalDto;
import com.be3c.sysmetic.domain.strategy.service.AdminStrategyService;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.common.response.SuccessCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StrategyAllowController {

    private final AdminStrategyService adminStrategyService;

    @GetMapping("/admin/strategy")
    public ResponseEntity<ApiResponse<PageResponse<AdminStrategyGetResponseDto>>> getAdminStrategy(
            @RequestParam Integer page
    ) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(adminStrategyService.findStrategyPage(page)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(ErrorCode.NOT_FOUND));
        }
    }

//    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/admin/strategy/approval")
    public ResponseEntity<ApiResponse<PageResponse<AdminStrategyApprovalGetResponseDto>>> strategyApproval(
            @RequestParam Integer page
    ) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(adminStrategyService.findApproveStrategyPage(page)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(ErrorCode.NOT_FOUND));
        } catch (AuthenticationCredentialsNotFoundException |
                 UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN, e.getMessage()));
        }
    }

//    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PatchMapping("/admin/strategy/allow")
    public ResponseEntity<ApiResponse<String>> strategyAllow(
            @RequestParam Long id
    ) throws Exception{
        try {
            adminStrategyService.StrategyApproveApplyAllow(id);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(SuccessCode.OK, "해당 전략이 승인되었습니다."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(ErrorCode.NOT_FOUND));
        } catch (AuthenticationCredentialsNotFoundException |
                 UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN, e.getMessage()));
        }
    }

    @PatchMapping("/admin/strategy/reject/")
    public ResponseEntity<ApiResponse<String>> strategyReject(
            @ModelAttribute RejectStrategyApprovalDto rejectStrategyApprovalDto
    ) {
        try {
            if(adminStrategyService.rejectStrategyApproval(rejectStrategyApprovalDto)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(ErrorCode.NOT_FOUND));
        }
    }
}
