package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.InsertStrategyRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StrategyService {

    // 임시
    Strategy findOne(Long id);

    @Transactional
    Strategy insertStrategy(InsertStrategyRequestDto requestDto);

    boolean confirmDuplicationName(String name);
}
