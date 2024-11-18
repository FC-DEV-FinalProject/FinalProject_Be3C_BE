package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StrategyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;

public interface UpdateStrategyService {
    Strategy updateStrategy(Long id, StrategyPostRequestDto requestDto);
}
