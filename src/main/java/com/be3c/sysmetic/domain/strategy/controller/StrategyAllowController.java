package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.service.AdminStrategyService;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity<ApiResponse<PageResponse<AdminStrategyGetResponseDto>>> strategyApproval(
            @RequestParam Integer page
    ) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(adminStrategyService.findApproveStrategyPage(page)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(ErrorCode.NOT_FOUND));
        }
    }
}
