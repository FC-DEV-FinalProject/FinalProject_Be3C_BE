package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.StrategyListByTraderDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyListDto;
import com.be3c.sysmetic.domain.strategy.dto.TraderNicknameListDto;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "전략 목록 API", description = "사용자의 전략 목록 조회 요청 API")
public interface StrategyListControllerDocs {

    @Operation(
            summary = "전략 목록 API",
            description = "전략 탐색 시 디폴트로 보이는 전략 목록 API - 누적수익률 내림차순 정렬",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "401")
            }
    )
    @GetMapping("/strategy/list")
    APIResponse<PageResponse<StrategyListDto>> getStrategies(
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum);


    @Operation(
            summary = "전략명 검색 API",
            description = "전략명 검색 API - 검색 키워드를 포함하는 전략, 공개 전략 개수 내림차순 정렬",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "4001")
            }
    )
    @GetMapping("/strategy/trader")
    APIResponse<PageResponse<TraderNicknameListDto>> searchByTraderNickname(
            @RequestParam("nickname") String nickname,
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum);


    @Operation(
            summary = "트레이더별 전략 목록 API",
            description = "트레이더별 전략 목록 API - 공개인 전략만 노출, 누적수익률순 내림차순 정렬",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "401")
            }
    )
    @GetMapping("/strategy/choose-trader")
    APIResponse<PageResponse<StrategyListByTraderDto>> getStrategiesByTraderId(
            @RequestParam("traderId") Long traderId,
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum);
}
