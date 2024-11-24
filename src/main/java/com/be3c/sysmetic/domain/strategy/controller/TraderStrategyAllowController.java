package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.service.StrategyAllowApprovalService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.exception.ConflictException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TraderStrategyAllowController {

    private final StrategyAllowApprovalService strategyAllowApprovalService;

    @PostMapping("/strategy/approve-open/{id}")
    public ResponseEntity<APIResponse<String>> postApproveOpenStrategy(
            @NotBlank @PathVariable Long id
    ) {
        try {
            strategyAllowApprovalService.approveOpen(id);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.success());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST));
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(APIResponse.fail(ErrorCode.DUPLICATE_RESOURCE));
        }
    }

    @PatchMapping("/strategy/approve-cancel/{id}")
    public ResponseEntity<APIResponse<String>> postApproveCancelStrategy(
            @NotBlank @PathVariable Long id
    ) {
        try {
            strategyAllowApprovalService.approveCancel(id);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.success());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }
}
