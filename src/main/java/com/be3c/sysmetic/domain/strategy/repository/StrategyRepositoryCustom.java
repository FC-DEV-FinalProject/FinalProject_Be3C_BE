package com.be3c.sysmetic.domain.strategy.repository;


import com.be3c.sysmetic.domain.strategy.dto.StrategyAnalysisOption;
import com.be3c.sysmetic.domain.strategy.dto.StrategyAnalysisResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategySearchRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Daily;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

// QueryDSL로 작성된 메서드를 구현하기
public interface StrategyRepositoryCustom {

    // searchByConditions : 항목별 상세 조건으로 검색하기 - 공개인 전략만
    Page<Strategy> searchByConditions(Pageable pageable, StrategySearchRequestDto strategySearchRequestDto);

    // searchByAlgorithm : 알고리즘별 검색
    Page<Strategy> searchByAlgorithm(Pageable pageable, String algorithm);

    // findGraphAnalysis : 분석 그래프 데이터
    StrategyAnalysisResponseDto findGraphAnalysis(Long id, StrategyAnalysisOption optionOne, StrategyAnalysisOption optionTwo, String period);
}
