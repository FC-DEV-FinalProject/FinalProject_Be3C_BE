package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.StrategyAlgorithmOption;
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
             "methods": ["메뉴얼", "시스템", "하이브리드"],
             "cycle": ["D", "P"],
             "stockNames": ["국내주식", "해외주식", "국내ETF", "해외ETF", "국내채권", "해외채권", "국내리츠, "해외리츠"],
             "accumulatedProfitLossRateRangeStart": "10",
             "accumulatedProfitLossRateRangeEnd": "100"
        }
    */
    @Operation(
            summary = "전략 상세 조건 검색",
            description = "전략 목록에서 항목별 상세 조건을 검색합니다.<br><br>" +
                    "Request Body 예시: <br><br><br>" +
                    "{ \"methods\": [\"메뉴얼\", \"시스템\", \"하이브리드\"], \"cycle\": [\"D\", \"P\"], \"stockNames\": [\"국내주식\", \"해외주식\", \"국내ETF\", \"해외ETF\", \"국내채권\", \"해외채권\", \"국내리츠\", \"해외리츠\"], \"accumulatedProfitLossRateRangeStart\": \"90\",  \"accumulatedProfitLossRateRangeEnd\": \"100\" }",
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
            @RequestBody StrategySearchRequestDto strategySearchRequestDto);


    /*
        algorithmSearch : 알고리즘별 전략 검색
    */
    @Operation(
            summary = "알고리즘별 전략 - DEFENSIVE는 아직 조회 못합니다",
            description = "전략 목록에서 항목ㅋ별 상세 조건을 검색합니다.<br><br>" +
                    "Request Param 설명 :<br><br>" +
                    "pageNum - defaultValue 0, 페이지 이동 시 값 입력 <br><br>" +
                    "algorithm - defaultValue EFFICIENCY -> Algorithm 선택 - EFFICIENCY, OFFENSIVE, DEFENSIVE"
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
            @RequestParam(name = "algorithm", defaultValue = "EFFICIENCY") StrategyAlgorithmOption algorithm);
}
