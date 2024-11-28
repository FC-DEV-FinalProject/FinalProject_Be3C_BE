package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.AccountImageResponseDto;
import com.be3c.sysmetic.domain.strategy.service.*;
import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.common.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "전략 API", description = "사용자 전략")
@RequestMapping("/v1/strategy")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RestController
public class StrategyController {

    private final DailyServiceImpl dailyService;
    private final MonthlyServiceImpl monthlyService;
    private final AccountImageServiceImpl accountImageService;
    private final StrategyStatisticsServiceImpl strategyStatisticsService;

    // 일간데이터 조회
    @Operation(
            summary = "일간분석 조회",
            description = "모든 회원이 접근 가능한 PUBLIC 상태인 전략의 일간분석 데이터 조회"
    )
    @GetMapping("/strategy/daily/{strategyId}")
    public ResponseEntity<APIResponse<PageResponse<DailyGetResponseDto>>> findDaily(
            @PathVariable Long strategyId,
            @RequestParam("page") Integer page,
            @RequestParam(value = "startDate", required = false) @Schema(description = "조회 시작 년월일", example = "2024-11-01") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @Schema(description = "조회 종료 년월일", example = "2024-11-26") LocalDate endDate
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(dailyService.findDaily(strategyId, page, startDate, endDate)));
    }

    // 월간데이터 조회
    @Operation(
            summary = "월간분석 조회",
            description = "모든 회원이 접근 가능한 PUBLIC 상태인 전략의 월간분석 데이터 조회"
    )
    @GetMapping("/strategy/monthly/{strategyId}")
    public ResponseEntity<APIResponse<PageResponse<MonthlyGetResponseDto>>> findMonthly(
            @PathVariable Long strategyId,
            @RequestParam("page") Integer page,
            @RequestParam(value = "startYearMonth", required = false) @Schema(description = "조회 시작 년월", example = "2024-01") String startYearMonth,
            @RequestParam(value = "endYearMonth", required = false) @Schema(description = "조회 종료 년월", example = "2024-10") String endYearMonth
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(monthlyService.findMonthly(strategyId, page, startYearMonth, endYearMonth)));
    }

    // 실계좌이미지 조회
    @Operation(
            summary = "실계좌이미지 조회",
            description = "모든 회원이 접근 가능한 PUBLIC 상태인 전략의 실계좌이미지 조회"
    )
    @GetMapping("/strategy/account-image/{strategyId}")
    public ResponseEntity<APIResponse<PageResponse<AccountImageResponseDto>>> getAccountImage(
            @PathVariable Long strategyId,
            @RequestParam Integer page
    ) {
        PageResponse<AccountImageResponseDto> responseDto = accountImageService.findAccountImages(strategyId, page);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(responseDto));
    }

    // 통계 조회
    @Operation(
            summary = "통계 조회",
            description = "모든 회원이 접근 가능한 PUBLIC 상태인 전략의 통계 조회"
    )
    @GetMapping("/strategy/statistics/{strategyId}")
    public ResponseEntity<APIResponse<StrategyStatisticsGetResponseDto>> findStatistics(@PathVariable Long strategyId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(strategyStatisticsService.findStrategyStatistics(strategyId)));
    }

}
