package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.domain.strategy.service.DailyServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.MethodServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.MonthlyServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.StockServiceImpl;
import com.be3c.sysmetic.global.common.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "전략 API", description = "사용자 전략 API")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RestController
public class StrategyController {

    private final DailyServiceImpl dailyService;
    private final MonthlyServiceImpl monthlyService;

    private final MethodServiceImpl methodService;
    private final StockServiceImpl stockService;

    // 매매방식 조회
    @Operation(summary = "매매방식 조회", description = "사용중인 매매방식 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 요청 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MethodGetResponseDto.class))
    )
    @GetMapping("/strategy/method-list")
    public ResponseEntity<APIResponse<List<MethodGetResponseDto>>> findMethodList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(methodService.findAllUsingMethod()));
    }

    // 종목 조회
    @Operation(summary = "종목 조회", description = "사용중인 종목 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 요청 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StockResponseDto.class))
    )
    @GetMapping("/strategy/stock-list")
    public ResponseEntity<APIResponse<List<StockGetResponseDto>>> findStockList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(stockService.findAllUsingStock()));
    }

    // 일간데이터 조회
    @Operation(summary = "일간분석 조회", description = "특정 전략의 일간분석을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 요청 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DailyGetResponseDto.class))
    )
    @GetMapping("/strategy/daily")
    public ResponseEntity<APIResponse> findDaily(@RequestParam("strategyId") Long strategyId, @RequestParam("page") int page, @RequestParam(value = "startDate", required = false) LocalDateTime startDate, @RequestParam(value = "endDate", required = false) LocalDateTime endDate) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(dailyService.findDaily(strategyId, page, startDate, endDate)));
    }

    @Operation(summary = "월간분석 조회", description = "특정 전략의 월간분석을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 요청 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MonthlyGetResponseDto.class))
    )
    // 월간데이터 조회
    @GetMapping("/strategy/monthly")
    public ResponseEntity<APIResponse> findMonthly(@RequestParam("strategyId") Long strategyId, @RequestParam("page") Integer page, @RequestParam(value = "startYear", required = false) Integer startYear, @RequestParam(value = "startMonth", required = false) Integer startMonth, @RequestParam(value = "endYear", required = false) Integer endYear, @RequestParam(value = "endMonth", required = false) Integer endMonth) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(monthlyService.findMonthly(strategyId, page, startYear, startMonth, endYear, endMonth)));
    }

}
