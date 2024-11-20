package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.SaveStrategyRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;

import java.util.List;

public interface UpdateStrategyService {
    Strategy updateStrategy(Long id, SaveStrategyRequestDto requestDto);
}