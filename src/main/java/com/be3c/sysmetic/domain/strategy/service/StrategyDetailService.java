package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StrategyAnalysisOption;
import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailDto;

public interface StrategyDetailService {

    // 전략 상세 페이지 요청
    StrategyDetailDto getDetail(Long id);
}