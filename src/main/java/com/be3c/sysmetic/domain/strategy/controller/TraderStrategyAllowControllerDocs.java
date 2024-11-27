package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.service.StrategyAllowApprovalService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.exception.ConflictException;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "관리자 - 전략 관리 API", description = "전략 관리 페이지 기능")
public interface TraderStrategyAllowControllerDocs {

    @Operation(summary = "전략 공개 승인", description = "전략 ID를 통해 전략 공개를 승인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전략 공개 승인 성공", content = @Content(schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "404", description = "전략을 찾을 수 없음", content = @Content(schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "409", description = "중복된 리소스 충돌", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    })
    ResponseEntity<APIResponse<String>> postApproveOpenStrategy(
            @NotBlank @PathVariable Long id
    );

    @Operation(summary = "전략 공개 취소 승인", description = "전략 ID를 통해 전략 공개 취소를 승인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전략 공개 취소 승인 성공", content = @Content(schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "404", description = "전략을 찾을 수 없음", content = @Content(schema = @Schema(implementation = APIResponse.class)))
    })
    ResponseEntity<APIResponse<String>> postApproveCancelStrategy(
            @NotBlank @PathVariable Long id
    );
}
