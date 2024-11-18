package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.DailyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.DailyPostResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.service.DailyServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.DeleteStrategyServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.UpdateStrategyServiceImpl;
import com.be3c.sysmetic.domain.strategy.service.InsertStrategyServiceImpl;
import com.be3c.sysmetic.global.common.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "트레이더 전략 API", description = "트레이더 전략 API")
@RequestMapping("/trader")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RestController
public class TraderStrategyController {

    private final InsertStrategyServiceImpl insertStrategyService;
    private final UpdateStrategyServiceImpl updateStrategyService;
    private final DeleteStrategyServiceImpl deleteStrategyService;
    private final DailyServiceImpl dailyService;

    // 전략 등록
    @Operation(summary = "전략 등록", description = "트레이더가 자신의 전략을 등록하는 API")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "등록 요청 성공",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(responseCode = "401", description = "권한 없음으로 인한 실패",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/strategy")
    public ResponseEntity<APIResponse<Strategy>> insertStrategy(@Valid @RequestBody StrategyPostRequestDto requestDto) {
        insertStrategyService.insertStrategy(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    // 전략명 중복
    @GetMapping("/strategy/duplication-name")
    public ResponseEntity<APIResponse<Boolean>> checkDuplicationStrategyName(@RequestParam String name) {
        boolean isDuplication = insertStrategyService.returnIsDuplicationName(name);
        // 중복 true, 미중복 false 반환
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(isDuplication));
    }

    // 전략 수정
    @PatchMapping("/strategy")
    public ResponseEntity<APIResponse<Strategy>> updateStrategy(@RequestParam Long strategyId, @Valid @RequestBody StrategyPostRequestDto requestDto) {
        updateStrategyService.updateStrategy(strategyId, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    // 전략 삭제
    @DeleteMapping("/strategy")
    public ResponseEntity<APIResponse> deleteStrategy(@RequestParam Long strategyId) {
        deleteStrategyService.deleteStrategy(strategyId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    // 일간데이터 등록
    @PostMapping("/strategy/daily")
    public ResponseEntity<APIResponse> insertDaily(@RequestParam("strategyId") Long strategyId, @Valid @RequestBody List<DailyPostRequestDto> requestDtoList) {
        DailyPostResponseDto responseDto = dailyService.getIsDuplicate(strategyId, requestDtoList);
        dailyService.saveDaily(strategyId, requestDtoList);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(responseDto));
    }

    // 일간데이터 수정
    @PatchMapping("/strategy/daily")
    public ResponseEntity<APIResponse> updateDaily(@RequestParam("strategyId") Long strategyId, @RequestParam("dailyId") Long dailyId, @RequestBody DailyPostRequestDto requestDto) {
        dailyService.updateDaily(strategyId, dailyId, requestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    // 일간데이터 삭제
    @DeleteMapping("/strategy/daily")
    public ResponseEntity<APIResponse> deleteDaily(@RequestParam("strategyId") Long strategyId, @RequestParam("dailyId") Long dailyId) {
        dailyService.deleteDaily(strategyId, dailyId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

}

