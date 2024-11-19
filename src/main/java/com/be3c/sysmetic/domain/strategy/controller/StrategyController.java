package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.AccountImageRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.AccountImageResponseDto;
import com.be3c.sysmetic.domain.strategy.service.AccountImageServiceImpl;
import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.domain.strategy.service.DailyServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.MethodServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.MonthlyServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.StrategyStatisticsServiceImpl;
import com.be3c.sysmetic.global.common.response.PageResponse;
 import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.domain.strategy.service.StockServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "전략 API", description = "사용자 전략")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RestController
public class StrategyController {

    private final DailyServiceImpl dailyService;
    private final MonthlyServiceImpl monthlyService;
    private final AccountImageServiceImpl accountImageService;
    private final MethodServiceImpl methodService;
    private final StockServiceImpl stockService;
    private final StrategyStatisticsServiceImpl strategyStatisticsService;

    // 매매방식 조회
    @Operation(
            summary = "매매방식 조회",
            description = "사용중인 모든 매매방식 조회"
    )
    @GetMapping("/strategy/method-list")
    public ResponseEntity<APIResponse<List<MethodGetResponseDto>>> findMethodList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(methodService.findAllUsingMethod()));
    }

    // 종목 조회
    @Operation(
            summary = "종목 조회",
            description = "사용중인 모든 종목 조회"
    )
    @GetMapping("/strategy/stock-list")
    public ResponseEntity<APIResponse<List<StockGetResponseDto>>> findStockList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(stockService.findAllUsingStock()));
    }

    // 일간데이터 조회
    @Operation(
            summary = "일간분석 조회",
            description = "일간분석 목록 조회"
    )
    @GetMapping("/strategy/daily")
    public ResponseEntity<APIResponse<PageResponse<DailyGetResponseDto>>> findDaily(
            @RequestParam("strategyId") Long strategyId,
            @RequestParam("page") Integer page,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(dailyService.findDaily(strategyId, page, startDate, endDate)));
    }

    // 월간데이터 조회
    @Operation(
            summary = "월간분석 조회",
            description = "월간분석 목록 조회"
    )
    @GetMapping("/strategy/monthly")
    public ResponseEntity<APIResponse<PageResponse<MonthlyGetResponseDto>>> findMonthly(
            @RequestParam("strategyId") Long strategyId,
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
            description = "실계좌이미지 목록 조회"
    )
    @GetMapping("/strategy/account-image")
    public ResponseEntity<APIResponse<PageResponse<AccountImageResponseDto>>> getAccountImage(
            @RequestParam Long strategyId,
            @RequestParam Integer page
    ) {
        PageResponse<AccountImageResponseDto> responseDto = accountImageService.findAccountImages(strategyId, page);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.success(responseDto));
    }

    // 실계좌이미지 삭제
    @Operation(
            summary = "실계좌이미지 삭제",
            description = "트레이더가 본인의 실계좌이미지 삭제"
    )
    @DeleteMapping("/strategy/account-image/{accountImageId}")
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
    @PostMapping(value = "/strategy/account-image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<APIResponse> saveAccountImage(
            @RequestPart List<AccountImageRequestDto> accountImages,
            @RequestParam Long strategyId
    ) {
        accountImageService.saveAccountImage(strategyId, accountImages);
        return ResponseEntity.ok(APIResponse.success());
    }

    // 통계 조회
    @Operation(
            summary = "통계 조회",
            description = "특정 전략의 통계 조회",
            responses = {
                    @ApiResponse(content = @Content(schema = @Schema(implementation = StrategyStatisticsGetResponseDto.class)))
            }
    )
    @GetMapping("/strategy/statistics/{strategyId}")
    public ResponseEntity<APIResponse<StrategyStatisticsGetResponseDto>> findStatistics(@PathVariable Long strategyId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(strategyStatisticsService.findStrategyStatistics(strategyId)));
    }

}
