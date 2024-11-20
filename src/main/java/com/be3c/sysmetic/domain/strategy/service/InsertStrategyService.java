package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.SaveStrategyRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.transaction.annotation.Transactional;

public interface InsertStrategyService {
    @Transactional
    Strategy insertStrategy(SaveStrategyRequestDto requestDto);

    boolean returnIsDuplicationName(String name);
}