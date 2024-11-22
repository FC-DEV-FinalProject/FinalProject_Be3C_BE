package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StrategyAlgorithmResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategySearchRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategySearchResponseDto;
import com.be3c.sysmetic.global.common.response.PageResponse;

public interface StrategySearchService {

    // searchConditions : 상세 조건 검색
    PageResponse<StrategySearchResponseDto> searchConditions(
            Integer pageNum,
            StrategySearchRequestDto strategySearchRequestDto);

    // searchAlgorithm : 알고리즘별 검색
    PageResponse<StrategyAlgorithmResponseDto> searchAlgorithm(
            Integer pageNum,
            String type);
}
