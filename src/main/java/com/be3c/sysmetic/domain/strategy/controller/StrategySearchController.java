package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.StrategyAlgorithmResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategySearchRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategySearchResponseDto;
import com.be3c.sysmetic.domain.strategy.service.StrategySearchService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategySearchController implements StrategySearchControllerDocs {

    private final StrategySearchService strategySearchService;

    /*
        conditionSearch : 항목별 상세 검색
        URL 경로 : localhost:8080/strategy/search-conditions?pageNum=0
        HTTP Method : POST
        Request Body : JSON
        {
             "methods": ["Manual"],
             "cycle": ["D"],
             "stockNames": [],
             "accumProfitLossRateRange": ["90~100%"]
        }
    */
    @Override
    @PostMapping("/strategy/search-conditions")
    public APIResponse<PageResponse<StrategySearchResponseDto>> conditionSearch(
            @RequestHeader(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestBody StrategySearchRequestDto strategySearchRequestDto) throws Exception {

        // log.info("controller log=pageNum={}, strategySearchRequestDto={}", pageNum, strategySearchRequestDto);

        PageResponse<StrategySearchResponseDto> results = strategySearchService.searchConditions(pageNum, strategySearchRequestDto);

        if (results.getContent().isEmpty())
            return APIResponse.fail(ErrorCode.NOT_FOUND, "상세 조건에 해당하는 검색 결과가 없습니다.");

        return APIResponse.success(results);
    }


    /*
        algorithmSearch : 알고리즘별 정렬 - 계산 후 정렬 & 페이징
    */
    @Override
    @GetMapping("/strategy/search-algorithm")
    public APIResponse<PageResponse<StrategyAlgorithmResponseDto>> algorithmSearch(
            @RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(name = "type") String type) throws Exception {

        PageResponse<StrategyAlgorithmResponseDto> results = strategySearchService.searchAlgorithm(pageNum, type);

        if (results.getContent().isEmpty())
            return APIResponse.fail(ErrorCode.NOT_FOUND, type + "에 해당하는 전략이 없습니다.");

        return APIResponse.success(results);
    }


}