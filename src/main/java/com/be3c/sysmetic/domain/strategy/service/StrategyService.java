package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.MethodAndStockGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyDetailDto;

public interface StrategyService {
    StrategyDetailDto getStrategy(Long id);
    boolean privateStrategy(Long id);
    MethodAndStockGetResponseDto findMethodAndStock();
}
