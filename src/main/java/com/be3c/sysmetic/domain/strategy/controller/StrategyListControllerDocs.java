package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.StrategyListByNameDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyListByTraderDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyListDto;
import com.be3c.sysmetic.domain.strategy.dto.TraderNicknameListDto;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "전략 목록 API", description = "사용자의 전략 목록 조회 요청 API")
public interface StrategyListControllerDocs {

    @Operation(
            summary = "전략 목록 API",
            description = "전략 탐색 전략 목록 API - 누적수익률 내림차순 정렬 <br>" +
                            "Request Param : <br>" +
                            "pageNum - defaultValue 0",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400")
            }
    )
    APIResponse<PageResponse<StrategyListDto>> getStrategies(
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum);


    @Operation(
            summary = "트레이더 닉네임 검색 API",
            description = "트레이더 닉네임 검색 API - 공개 전략 개수 내림차순 정렬. (동일한 트레이더가 중복 조회되면 DM 부탁드립니다!) <br>" +
                            "Request Param : <br>" +
                            "nickname - 검색할 닉네임 <br>" +
                            "pageNum - defaultValue 0",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400")
            }
    )
    APIResponse<PageResponse<TraderNicknameListDto>> searchByTraderNickname(
            @RequestParam("nickname") String nickname,
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum);


    @Operation(
            summary = "트레이더별 전략 목록 API",
            description = "트레이더별 전략 목록 API - 공개인 전략만 노출, 누적수익률순 내림차순 정렬 <br> " +
                            "Request Param : <br> " +
                            "traderId - 트레이더 일련번호 " +
                            "pageNum - defaultValue 0",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400")
            }
    )
    APIResponse<PageResponse<StrategyListByTraderDto>> getStrategiesByTraderId(
            @RequestParam("traderId") Long traderId,
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum);


    @Operation(
            summary = "전략명 검색 API",
            description = "전략명 검색 후 목록 API - 공개인 전략에 한해서 검색, 누적수익률순 내림차순 정렬 <br> " +
                    "Request Param : <br> " +
                    "name - 검색할 전략명 " +
                    "pageNum - defaultValue 0",
            responses = {
                    @ApiResponse(responseCode = "200") ,
                    @ApiResponse(responseCode = "400")
            }
    )
    APIResponse<PageResponse<StrategyListByNameDto>> getStrategiesByName(
            @RequestParam("keyword") String keyword,
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum);

}
