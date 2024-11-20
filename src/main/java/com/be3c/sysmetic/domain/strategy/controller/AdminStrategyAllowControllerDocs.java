package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.AdminStrategyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.AdminStrategySearchGetDto;
import com.be3c.sysmetic.domain.strategy.dto.AllowApprovalRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.RejectStrategyApprovalDto;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.common.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.NoSuchElementException;

<<<<<<<< HEAD:src/main/java/com/be3c/sysmetic/domain/strategy/controller/AdminStrategyAllowControllerDocs.java
@Tag(name = "관리자 전략 관리 API", description = "관리자 전략 관리")
public interface AdminStrategyAllowControllerDocs {
========
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminStrategyAllowController {

    private final AdminStrategyService adminStrategyService;
>>>>>>>> cf5bc71 (feat: 전략 공개 승인 요청 및 비공개 전환 api 작성):src/main/java/com/be3c/sysmetic/domain/strategy/controller/AdminStrategyAllowController.java

    /*
        관리자 전략 관리 페이지 api
        1. 데이터를 찾는 데 성공했을 때 : OK
        2. 페이지 내에 한 개의 전략도 존재하지 않을 때 : NOT_FOUND
     */
    @Operation(
            summary = "관리자 전략 관리",
            description = "관리자 페이지에서 전략 데이터를 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "데이터를 찾는 데 성공 (OK)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "페이지 내에 한 개의 전략도 존재하지 않음 (NOT_FOUND)",
                    content = @Content(mediaType = "application/json")
            )
    })
//    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<APIResponse<PageResponse<AdminStrategyGetResponseDto>>> getAdminStrategy(
            AdminStrategySearchGetDto adminStrategySearchGetDto
    );

    /*
        선택(단일 포함) 전략 승인 api
        1. 요청한 모든 전략 승인에 성공했을 때 : OK
        2. 요청한 전략 중 승인에 실패했을 때 : MULTI_STATUS
     */
    @Operation(
            summary = "선택(단일 포함) 전략 승인",
            description = "관리자가 요청한 전략을 승인하는 API (단일 또는 다중 승인 포함)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "요청한 모든 전략 승인 성공 (OK)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "207",
                    description = "요청한 전략 중 일부만 승인 성공 (MULTI_STATUS)",
                    content = @Content(mediaType = "application/json")
            )
    })
//    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<APIResponse<Map<Long, String>>> strategyAllow(
            @Valid @RequestBody AllowApprovalRequestDto allowApprovalRequestDto
    );

    /*
        단일 전략 공개 요청 거절 api
        1. 전략 공게 요청 거절에 성공했을 때 : OK
        2. 전략 공개 요청 거절에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 해당 전략의 공개 요청을 찾지 못했을 때 : NOT_FOUND
     */
    @Operation(
            summary = "단일 전략 공개 요청 거절",
            description = "단일 전략의 공개 요청을 거절하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "전략 공개 요청 거절 성공 (OK)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "전략 공개 요청 거절 실패 (INTERNAL_SERVER_ERROR)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 전략의 공개 요청을 찾지 못함 (NOT_FOUND)",
                    content = @Content(mediaType = "application/json")
            )
    })
//    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<APIResponse<String>> strategyReject(
            @Valid RejectStrategyApprovalDto rejectStrategyApprovalDto
    );
}
