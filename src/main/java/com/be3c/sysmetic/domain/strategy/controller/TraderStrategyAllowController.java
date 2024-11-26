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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TraderStrategyAllowController {

    private final StrategyAllowApprovalService strategyAllowApprovalService;

    @Operation(summary = "전략 공개 승인", description = "전략 ID를 통해 전략 공개를 승인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전략 공개 승인 성공", content = @Content(schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "404", description = "전략을 찾을 수 없음", content = @Content(schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "409", description = "중복된 리소스 충돌", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    })
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

    @Operation(summary = "전략 공개 취소 승인", description = "전략 ID를 통해 전략 공개 취소를 승인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전략 공개 취소 승인 성공", content = @Content(schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "404", description = "전략을 찾을 수 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    })
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
