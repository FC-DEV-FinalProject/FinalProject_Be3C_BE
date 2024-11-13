package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.service.DailyServiceImpl;
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

    @GetMapping("/strategy/daily")
    public ResponseEntity<ApiResponse> findDaily(@RequestParam("strategyId") Long strategyId, @RequestParam("page") int page, @RequestParam(value = "startDate", required = false) LocalDateTime startDate, @RequestParam(value = "endDate", required = false) LocalDateTime endDate) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(dailyService.findDaily(strategyId, page, startDate, endDate)));
    }

}
