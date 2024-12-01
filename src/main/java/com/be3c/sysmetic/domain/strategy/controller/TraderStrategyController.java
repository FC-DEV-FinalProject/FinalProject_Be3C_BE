package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.domain.strategy.dto.AccountImageRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.DailyPostResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.service.*;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "트레이더 전략 API", description = "트레이더 전략 관리 및 조회")
@RequestMapping("/v1/trader")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RestController
public class TraderStrategyController {

    private final TraderStrategyServiceImpl traderStrategyService;
    private final DailyServiceImpl dailyService;
    private final MonthlyServiceImpl monthlyService;
    private final StrategyStatisticsServiceImpl strategyStatisticsService;
    private final AccountImageServiceImpl accountImageService;
    private final StrategyServiceImpl strategyService;

    // 전략 등록
    @Operation(
            summary = "전략 등록",
            description = "트레이더가 본인의 전략을 등록"
    )
    @PostMapping(value = "/strategy", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    // @PreAuthorize("hasRole('ROLE_TRADER'))
    public ResponseEntity<APIResponse<StrategyPostResponseDto>> insertStrategy(
            @RequestPart("requestDto") @Parameter(description = "전략 등록 요청 DTO") @Valid StrategyPostRequestDto requestDto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(traderStrategyService.insertStrategy(requestDto, file)));
    }

    // 전략 수정
    @Operation(
            summary = "전략 수정",
            description = "트레이더가 본인의 전략을 수정"
    )
    @PatchMapping(value = "/strategy/{strategyId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    // @PreAuthorize("hasRole('ROLE_TRADER'))
    public ResponseEntity<APIResponse> updateStrategy(
            @PathVariable Long strategyId,
            @RequestPart @Valid StrategyPostRequestDto requestDto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        traderStrategyService.updateStrategy(strategyId, requestDto, file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    // 전략 삭제
    @Operation(
            summary = "전략 삭제",
            description = "트레이더가 본인의 전략을 삭제"
    )
    @DeleteMapping("/strategy/{strategyId}")
    // @PreAuthorize("hasRole('ROLE_TRADER')")
    public ResponseEntity<APIResponse> deleteStrategy(@PathVariable Long strategyId) {
        traderStrategyService.deleteStrategy(strategyId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    // 일간분석 등록
    @Operation(
            summary = "일간분석 등록",
            description = "트레이더가 본인의 일간분석 데이터를 등록"
    )
    // @PreAuthorize("hasRole('ROLE_TRADER')")
    @PostMapping("/strategy/daily/{strategyId}")
    public ResponseEntity<APIResponse> insertDaily(
            @PathVariable Long strategyId,
            @Valid @RequestBody List<DailyRequestDto> requestDtoList
    ) {
        DailyPostResponseDto responseDto = dailyService.getIsDuplicate(strategyId, requestDtoList);
        dailyService.saveDaily(strategyId, requestDtoList);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(responseDto));
    }

    // 일간데이터 수정
    @Operation(
            summary = "일간분석 수정",
            description = "트레이더가 본인의 일간분석 데이터를 수정"
    )
    @PatchMapping("/strategy/daily/{dailyId}")
    // @PreAuthorize("hasRole('ROLE_TRADER')")
    public ResponseEntity<APIResponse> updateDaily(
            @PathVariable Long dailyId,
            @RequestBody DailyRequestDto requestDto
    ) {
        dailyService.updateDaily(dailyId, requestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    // 일간데이터 삭제
    @Operation(
            summary = "일간분석 삭제",
            description = "트레이더가 본인의 일간분석 데이터를 삭제"
    )
    @DeleteMapping("/strategy/daily/{dailyId}")
    // @PreAuthorize("hasRole('ROLE_TRADER')")
    public ResponseEntity<APIResponse> deleteDaily(@PathVariable Long dailyId) {
        dailyService.deleteDaily(dailyId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    // 실계좌이미지 삭제
    @Operation(
            summary = "실계좌이미지 삭제",
            description = "트레이더가 본인의 실계좌이미지 삭제"
    )
    @DeleteMapping("/strategy/account-image/{accountImageId}")
    // @PreAuthorize("hasRole('ROLE_TRADER')")
    public ResponseEntity<APIResponse> deleteAccountImage(@PathVariable Long accountImageId) {
        accountImageService.deleteAccountImage(accountImageId);
        return ResponseEntity.ok(APIResponse.success());
    }

    // todo. 파일 관련 작업 필요. 예슬님이 이어서 작업해주실 예정입니다.
    // 실계좌이미지 등록
    @Operation(
            summary = "실계좌이미지 등록",
            description = "트레이더가 본인의 실계좌이미지 등록"
    )
    @PostMapping(value = "/strategy/account-image/{strategyId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    // @PreAuthorize("hasRole('ROLE_TRADER')")
    public ResponseEntity<APIResponse> saveAccountImage(
            @RequestPart List<AccountImageRequestDto> accountImages,
            @PathVariable Long strategyId
    ) {
        accountImageService.saveAccountImage(strategyId, accountImages);
        return ResponseEntity.ok(APIResponse.success());
    }

    // 전략 관리 화면 매매방식, 종목 조회
    @Operation(
            summary = "매매방식, 종목 조회",
            description = "사용중인 모든 매매방식 및 종목 조회"
    )
    @GetMapping("/strategy/method-and-stock")
    public ResponseEntity<APIResponse<MethodAndStockGetResponseDto>> findMethodAndStockList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(strategyService.findMethodAndStock()));
    }

    // 비공개 전환
    @Operation(
            summary = "비공개 전환",
            description = "트레이더가 본인의 전략을 비공개로 전환"
    )
    @PatchMapping("/strategy/{id}/visibility")
    public ResponseEntity<APIResponse<String>> patchStrategy(
            @NotBlank @PathVariable Long id
    ) {
        try {
            if(strategyService.privateStrategy(id)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }

}

