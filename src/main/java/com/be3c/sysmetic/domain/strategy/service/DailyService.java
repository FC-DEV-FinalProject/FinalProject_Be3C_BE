package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.SaveDailyRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Daily;

import java.util.List;

public interface DailyService {
    void saveDaily(Long strategyId, List<SaveDailyRequestDto> requestDtoList);
    boolean isDuplication(Long strategyId, List<SaveDailyRequestDto> requestDtoList);
    void updateDaily(Long strategyId, Long dailyId, SaveDailyRequestDto requestDto);
}
