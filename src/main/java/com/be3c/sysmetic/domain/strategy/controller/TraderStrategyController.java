package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.SaveDailyRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.SaveDailyResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.SaveStrategyRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.service.DailyServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.DeleteStrategyServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.UpdateStrategyServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.InsertStrategyServiceImpl;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/trader")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RestController
public class TraderStrategyController {

    private final InsertStrategyServiceImpl insertStrategyService;
    private final UpdateStrategyServiceImpl updateStrategyService;
    private final DeleteStrategyServiceImpl deleteStrategyService;
    private final DailyServiceImpl dailyService;

    // 전략 등록
    @PostMapping("/strategy")
    public ResponseEntity<ApiResponse<Strategy>> insertStrategy(@Valid @RequestBody SaveStrategyRequestDto requestDto) {
        insertStrategyService.insertStrategy(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success());
    }

    // 전략명 중복
    @GetMapping("/strategy/duplication-name")
    public ResponseEntity<ApiResponse<Boolean>> checkDuplicationStrategyName(@RequestParam String name) {
        boolean isDuplication = insertStrategyService.returnIsDuplicationName(name);
        // 중복 true, 미중복 false 반환
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(isDuplication));
    }

    // 전략 수정
    @PatchMapping("/strategy")
    public ResponseEntity<ApiResponse<Strategy>> updateStrategy(@RequestParam Long strategyId, @Valid @RequestBody SaveStrategyRequestDto requestDto) {
        updateStrategyService.updateStrategy(strategyId, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success());
    }

    // 전략 삭제
    @DeleteMapping("/strategy")
    public ResponseEntity<ApiResponse> deleteStrategy(@RequestParam Long strategyId) {
        deleteStrategyService.deleteStrategy(strategyId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success());
    }

    // 일간데이터 등록
    @PostMapping("/strategy/daily")
    public ResponseEntity<ApiResponse> insertDaily(@RequestParam("strategyId") Long strategyId, @Valid @RequestBody List<SaveDailyRequestDto> requestDtoList) {
        SaveDailyResponseDto responseDto = dailyService.getIsDuplicate(strategyId, requestDtoList);
        dailyService.saveDaily(strategyId, requestDtoList);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(responseDto));
    }

    // 일간데이터 수정
    @PatchMapping("/strategy/daily")
    public ResponseEntity<ApiResponse> updateDaily(@RequestParam("strategyId") Long strategyId, @RequestParam("dailyId") Long dailyId, @RequestBody SaveDailyRequestDto requestDto) {
        dailyService.updateDaily(strategyId, dailyId, requestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success());
    }

    @DeleteMapping("/strategy/daily")
    public ResponseEntity<ApiResponse> deleteDaily(@RequestParam("strategyId") Long strategyId, @RequestParam("dailyId") Long dailyId) {
        dailyService.deleteDaily(strategyId, dailyId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success());
    }

}

