package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.SaveDailyRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.SaveDailyResponseDto;

import java.util.List;

public interface DailyService {
    void saveDaily(Long strategyId, List<SaveDailyRequestDto> requestDtoList);
    SaveDailyResponseDto getIsDuplicate(Long strategyId, List<SaveDailyRequestDto> requestDtoList);
    void updateDaily(Long strategyId, Long dailyId, SaveDailyRequestDto requestDto);
    void deleteDaily(Long strategyId, Long dailyId);
}
