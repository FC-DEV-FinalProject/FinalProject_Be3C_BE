package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.service.DailyServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.MonthlyServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.StrategyStatisticsServiceImpl;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RestController
public class StrategyController {

    private final DailyServiceImpl dailyService;
    private final MonthlyServiceImpl monthlyService;
    private final StrategyStatisticsServiceImpl strategyStatisticsService;

    // todo : 페이지 response 변경 필요. -> 리팩토링시 진행

    // 일간데이터 조회
    @GetMapping("/strategy/daily")
    public ResponseEntity<ApiResponse> findDaily(@RequestParam("strategyId") Long strategyId, @RequestParam("page") int page, @RequestParam(value = "startDate", required = false) LocalDateTime startDate, @RequestParam(value = "endDate", required = false) LocalDateTime endDate) {
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
    @GetMapping("/strategy/statistics")
    public ResponseEntity<ApiResponse> findStatistics(@RequestParam("strategyId") Long strategyId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(strategyStatisticsService.findStrategyStatistics(strategyId)));
    }

}
