package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.SaveStrategyRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.service.UpdateStrategyServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.InsertStrategyServiceImpl;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RestController
public class StrategyController {

    private final InsertStrategyServiceImpl strategyService;
    private final UpdateStrategyServiceImpl strategyModificationService;

    // 전략 등록
    @PostMapping("/trader/strategy")
    public ResponseEntity<ApiResponse<Strategy>> insertStrategy(@Valid @RequestBody SaveStrategyRequestDto requestDto) {
        strategyService.insertStrategy(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success());
    }

    // 전략명 중복
    @GetMapping("/trader/strategy/duplication-name")
    public ResponseEntity<ApiResponse<Boolean>> checkDuplicationStrategyName(@RequestParam String name) {
        boolean isDuplication = strategyService.returnIsDuplicationName(name);
        // 중복 true, 미중복 false 반환
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(isDuplication));
    }

    // 전략 수정
    @PostMapping("/trader/strategy/update")
    public ResponseEntity<ApiResponse<Strategy>> updateStrategy(@RequestParam Long strategyId, @Valid @RequestBody SaveStrategyRequestDto requestDto) {
        strategyModificationService.updateStrategy(strategyId, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success());
    }

}
