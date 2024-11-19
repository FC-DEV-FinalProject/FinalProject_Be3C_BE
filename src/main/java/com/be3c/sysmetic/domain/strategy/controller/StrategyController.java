package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.service.DailyServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.MonthlyServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.StrategyStatisticsServiceImpl;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RestController
public class StrategyController {

    private final DailyServiceImpl dailyService;
    private final MonthlyServiceImpl monthlyService;
    private final StrategyStatisticsServiceImpl strategyStatisticsService;

    // todo : 페이지 response 변경 필요. -> 리팩토링시 진행

    // 일간데이터 조회
    @GetMapping("/strategy/daily")
    public ResponseEntity<ApiResponse> findDaily(@RequestParam("strategyId") Long strategyId, @RequestParam("page") int page, @RequestParam(value = "startDate", required = false) LocalDate startDate, @RequestParam(value = "endDate", required = false) LocalDate endDate) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(dailyService.findDaily(strategyId, page, startDate, endDate)));
    }

    // 월간데이터 조회
    @GetMapping("/strategy/monthly")
    public ResponseEntity<ApiResponse> findMonthly(@RequestParam("strategyId") Long strategyId, @RequestParam("page") Integer page, @RequestParam(value = "startYear", required = false) Integer startYear, @RequestParam(value = "startMonth", required = false) Integer startMonth, @RequestParam(value = "endYear", required = false) Integer endYear, @RequestParam(value = "endMonth", required = false) Integer endMonth) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(monthlyService.findMonthly(strategyId, page, startYear, startMonth, endYear, endMonth)));
    }

    // 통계 조회
    @GetMapping("/strategy/statistics/{strategyId}")
    public ResponseEntity<ApiResponse> findStatistics(@PathVariable Long strategyId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(strategyStatisticsService.findStrategyStatistics(strategyId)));
    }

}
