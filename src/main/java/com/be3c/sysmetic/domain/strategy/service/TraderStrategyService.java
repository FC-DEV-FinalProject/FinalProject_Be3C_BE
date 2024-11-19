package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StrategyPostRequestDto;

public interface TraderStrategyService {
    void insertStrategy(StrategyPostRequestDto requestDto);
    void updateStrategy(Long strategyId, StrategyPostRequestDto requestDto);
    void deleteStrategy(Long strategyId);
}
