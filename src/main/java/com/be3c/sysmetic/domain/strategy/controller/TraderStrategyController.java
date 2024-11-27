package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.domain.strategy.dto.AccountImageRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.DailyPostResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.service.*;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    public ResponseEntity<APIResponse> insertStrategy(
            @RequestPart("requestDto") @Parameter(description = "전략 등록 요청 DTO") @Valid StrategyPostRequestDto requestDto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        traderStrategyService.insertStrategy(requestDto, file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
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

    // 일간데이터 조회
    @Operation(
            summary = "일간분석 조회",
            description = "트레이더가 본인 전략의 일간분석 데이터를 조회하는 API로, public, private, pending approval 상태인 전략에 대해 일간분석 데이터 조회 가능"
    )
    @GetMapping("/strategy/daily/{strategyId}")
    // @PreAuthorize("hasRole('ROLE_TRADER') and hasRole('ROLE_ADMIN') and hasRole('ROLE_MANAGER')")
    public ResponseEntity<APIResponse<PageResponse<DailyGetResponseDto>>> findTraderDaily(
            @PathVariable Long strategyId,
            @RequestParam("page") Integer page,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(dailyService.findTraderDaily(strategyId, page, startDate, endDate)));
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

    // 월간데이터 조회
    @Operation(
            summary = "월간분석 조회",
            description = "트레이더가 본인 전략의 월간분석 데이터를 조회하는 API로, public, private, pending approval 상태인 전략에 대해 월간분석 데이터 조회 가능"
    )
    @GetMapping("/strategy/monthly/{strategyId}")
    // @PreAuthorize("hasRole('ROLE_TRADER') and hasRole('ROLE_ADMIN') and hasRole('ROLE_MANAGER')")
    public ResponseEntity<APIResponse<PageResponse<MonthlyGetResponseDto>>> findTraderMonthly(
            @PathVariable Long strategyId,
            @RequestParam("page") Integer page,
            @RequestParam(value = "startYearMonth", required = false) @Schema(description = "조회 시작 년월", example = "2024-01") String startYearMonth,
            @RequestParam(value = "endYearMonth", required = false) @Schema(description = "조회 종료 년월", example = "2024-10") String endYearMonth
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(monthlyService.findTraderMonthly(strategyId, page, startYearMonth, endYearMonth)));
    }

    // 통계 조회
    @Operation(
            summary = "통계 조회",
            description = "트레이더가 본인 전략의 통계를 조회하는 API로, public, private, pending approval 상태인 전략에 대해 통계 조회 가능"
    )
    @GetMapping("/strategy/statistics/{strategyId}")
    // @PreAuthorize("hasRole('ROLE_TRADER') and hasRole('ROLE_ADMIN') and hasRole('ROLE_MANAGER')")
    public ResponseEntity<APIResponse<StrategyStatisticsGetResponseDto>> findStatistics(@PathVariable Long strategyId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(strategyStatisticsService.findTraderStrategyStatistics(strategyId)));
    }

    // 실계좌이미지 조회
    @Operation(
            summary = "실계좌이미지 조회",
            description = "트레이더가 본인 전략의 실계좌 이미지를 조회하는 API로, public, private, pending approval 상태인 전략에 대해 통계 조회 가능"
    )
    @GetMapping("/strategy/account-image/{strategyId}")
    // @PreAuthorize("hasRole('ROLE_TRADER') and hasRole('ROLE_ADMIN') and hasRole('ROLE_MANAGER')")
    public ResponseEntity<APIResponse<PageResponse<AccountImageResponseDto>>> getAccountImage(
            @PathVariable Long strategyId,
            @RequestParam Integer page
    ) {
        PageResponse<AccountImageResponseDto> responseDto = accountImageService.findTraderAccountImages(strategyId, page);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(responseDto));
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

}

