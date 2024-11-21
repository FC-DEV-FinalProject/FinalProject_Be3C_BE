package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.MethodAndStockGetResponseDto;

public interface StrategyService {
    boolean privateStrategy(Long id);
    MethodAndStockGetResponseDto findMethodAndStock();
}
