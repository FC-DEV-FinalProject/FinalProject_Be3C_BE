package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.AccountImageRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.DailyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.DailyPostResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.service.*;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "전략 관리 API", description = "트레이더 전략 관리")
@RequestMapping("/v1/trader")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RestController
public class TraderStrategyController {

    private final TraderStrategyServiceImpl traderStrategyService;
    private final DailyServiceImpl dailyService;
    private final AccountImageServiceImpl accountImageService;
    private final StrategyServiceImpl strategyService;

    // 전략 등록
    @Operation(
            summary = "전략 등록",
            description = "트레이더가 본인의 전략을 등록"
    )
    @PostMapping(value = "/strategy", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<APIResponse> insertStrategy(
            @RequestPart("requestDto") @Parameter(description = "전략 등록 요청 DTO") @Valid StrategyPostRequestDto requestDto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        traderStrategyService.insertStrategy(requestDto, file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    // 전략 수정
    @Operation(
            summary = "전략 수정",
            description = "트레이더가 본인의 전략을 수정"
    )
    @PatchMapping(value = "/strategy/{strategyId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<APIResponse> updateStrategy(
            @PathVariable Long strategyId,
            @RequestPart @Valid StrategyPostRequestDto requestDto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        traderStrategyService.updateStrategy(strategyId, requestDto, file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    // 전략 삭제
    @Operation(
            summary = "전략 삭제",
            description = "트레이더가 본인의 전략을 삭제"
    )
    @DeleteMapping("/strategy/{strategyId}")
    public ResponseEntity<APIResponse> deleteStrategy(@PathVariable Long strategyId) {
        traderStrategyService.deleteStrategy(strategyId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    // 일간데이터 등록
    @Operation(
            summary = "일간분석 등록",
            description = "트레이더가 본인의 일간분석 데이터를 등록"
    )
    @PostMapping("/strategy/daily")
    public ResponseEntity<APIResponse> insertDaily(@RequestParam("strategyId") Long strategyId, @Valid @RequestBody List<DailyPostRequestDto> requestDtoList) {
        DailyPostResponseDto responseDto = dailyService.getIsDuplicate(strategyId, requestDtoList);
        dailyService.saveDaily(strategyId, requestDtoList);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success(responseDto));
    }

    // 일간데이터 수정
    @Operation(
            summary = "일간분석 수정",
            description = "트레이더가 본인의 일간분석 데이터를 수정"
    )
    @PatchMapping("/strategy/daily")
    public ResponseEntity<APIResponse> updateDaily(@RequestParam("strategyId") Long strategyId, @RequestParam("dailyId") Long dailyId, @RequestBody DailyPostRequestDto requestDto) {
        dailyService.updateDaily(strategyId, dailyId, requestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    // 일간데이터 삭제
    @Operation(
            summary = "일간분석 삭제",
            description = "트레이더가 본인의 일간분석 데이터를 삭제"
    )
    @DeleteMapping("/strategy/daily")
    public ResponseEntity<APIResponse> deleteDaily(@RequestParam("strategyId") Long strategyId, @RequestParam("dailyId") Long dailyId) {
        dailyService.deleteDaily(strategyId, dailyId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success());
    }

    // todo. 파일 관련 작업 필요. 예슬님이 이어서 작업해주실 예정입니다.
    // 실계좌이미지 등록
    @Operation(
            summary = "실계좌이미지 등록",
            description = "트레이더가 본인의 실계좌이미지 등록"
    )
    @PostMapping(value = "/strategy/account-image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<APIResponse> saveAccountImage(
            @RequestPart List<AccountImageRequestDto> requestDtoList,
            @RequestParam Long strategyId
    ) {
        accountImageService.saveAccountImage(strategyId, requestDtoList);
        return ResponseEntity.ok(APIResponse.success());
    }

    // 실계좌이미지 삭제
    @Operation(
            summary = "실계좌이미지 삭제",
            description = "트레이더가 본인의 실계좌이미지 삭제"
    )
    @DeleteMapping("/account-image/{accountImageId}")
    public ResponseEntity<APIResponse> deleteAccountImage(@PathVariable Long accountImageId) {
        accountImageService.deleteAccountImage(accountImageId);
        return ResponseEntity.ok(APIResponse.success());
    }

    // 비공개 전환
    @Operation(
            summary = "비공개 전환",
            description = "트레이더가 본인의 전략을 비공개로 전환"
    )
    @PatchMapping("/strategy/{id}/visibility")
    public ResponseEntity<APIResponse<String>> patchStrategy(
            @NotBlank @PathVariable Long id
    ) {
        try {
            if(strategyService.privateStrategy(id)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }

}

