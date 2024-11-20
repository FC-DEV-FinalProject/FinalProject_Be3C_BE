package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.InsertStrategyRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.service.StrategyServiceImpl;
import com.be3c.sysmetic.global.common.response.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RequiredArgsConstructor
@RestController
public class StrategyController {

    private final StrategyServiceImpl strategyService;

    // 전략 등록
    @PostMapping("/trader/strategy")
    public ResponseEntity<APIResponse<Strategy>> insertStrategy(@Valid @RequestBody InsertStrategyRequestDto requestDto) {
        strategyService.insertStrategy(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    // 전략명 중복
    @GetMapping("/trader/strategy/duplication-name")
    public ResponseEntity<APIResponse<Boolean>> checkDuplicationStrategyName(@RequestParam String name) {
        boolean isDuplication = strategyService.confirmDuplicationName(name);
        // 중복 true, 미중복 false 반환
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(isDuplication));
    }

}
