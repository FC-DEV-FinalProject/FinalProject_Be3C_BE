package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.StrategyAlgorithmResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategySearchRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategySearchResponseDto;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "전략 상세 검색 API", description = "전략 상세 검색")
public interface StrategySearchControllerDocs {

    /*
        conditionSearch : 항목별 상세 검색
        URL 경로 : localhost:8080/strategy/search-conditions?pageNum=0
        Body : JSON
        {
             "methods": ["Manual"],
             "cycle": ["D"],
             "stockNames": [],
             "accumProfitLossRateRangeStart": ["90"],
             "accumProfitLossRateRangeEnd": ["100"]
        }
    */
    @Operation(
            summary = "전략 상세 조건 검색",
            description = "전략 목록에서 항목별 상세 조건을 검색합니다.<br><br>" +
                    "Request Body 예시:<br><br>" +
                    "{ \"methods\": [\"Manual\"], \"cycle\": [\"D\"], \"stockNames\": [], \"accumProfitLossRateRangeStart\": \"90\",  \"accumProfitLossRateRangeEnd\": \"100\" }",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            responseCode = "401"
                    )
            }
    )
    @PostMapping("/strategy/search-conditions")
    APIResponse<PageResponse<StrategySearchResponseDto>> conditionSearch(
            @RequestHeader(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestBody StrategySearchRequestDto strategySearchRequestDto) throws Exception;


    /*
        algorithmSearch : 알고리즘별 전략 검색
    */
    @Operation(
            summary = "알고리즘별 전략 검색 - 아직 조회 안됩니다!!",
            description = "전략 목록에서 항목ㅋ별 상세 조건을 검색합니다.<br><br>" +
                    "Request Parma 설명 :<br><br>" +
                    "pageNum : defaultValue = 0, 페이지 이동 시 값 입력 <br><br>" +
                    "type : Algorithm 선택 - Efficiency, Offensive, Defensive"
            ,
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            responseCode = "401"
                    )
            }
    )
    @GetMapping("/strategy/search-algorithm")
    APIResponse<PageResponse<StrategyAlgorithmResponseDto>> algorithmSearch(
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(name = "type") String type) throws Exception;
}
