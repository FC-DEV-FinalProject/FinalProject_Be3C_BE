package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StrategyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.transaction.annotation.Transactional;

public interface InsertStrategyService {
    @Transactional
    Strategy insertStrategy(StrategyPostRequestDto requestDto);

    boolean returnIsDuplicationName(String name);
}
