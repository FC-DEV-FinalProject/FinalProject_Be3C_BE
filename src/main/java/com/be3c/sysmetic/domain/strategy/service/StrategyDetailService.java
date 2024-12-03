package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StrategyAnalysisOption;
import com.be3c.sysmetic.domain.strategy.dto.StrategyAnalysisResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailDto;
import com.be3c.sysmetic.domain.strategy.entity.Daily;
import com.be3c.sysmetic.global.common.response.APIResponse;

import java.time.LocalDate;

public interface StrategyDetailService {

    // 전략 상세 페이지 기본 정보 요청
    StrategyDetailDto getDetail(Long id);


    // 분석 지표 그래프 데이터 요청
    StrategyAnalysisResponseDto getAnalysis(Long strategyId, StrategyAnalysisOption optionOne, StrategyAnalysisOption optionTwo, String period);

    // 분석 그래프 데이터 등록
    APIResponse<String> saveAnalysis(Long id, LocalDate date);

    // 분석 그래프 데이터 수정
    APIResponse<String> updateAnalysis(Long strategyId, Long dailyId, LocalDate date);

    // 분석 그래프 데이터 삭제
    APIResponse<String> deleteAnalysis(Long strategyId, Long dailyId, Daily daily);
}