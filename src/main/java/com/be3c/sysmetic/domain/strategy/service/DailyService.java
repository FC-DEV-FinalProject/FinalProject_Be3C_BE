package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.DailyResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.SaveDailyRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.SaveDailyResponseDto;
import com.be3c.sysmetic.global.common.response.PageResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface DailyService {
    void saveDaily(Long strategyId, List<SaveDailyRequestDto> requestDtoList);
    SaveDailyResponseDto getIsDuplicate(Long strategyId, List<SaveDailyRequestDto> requestDtoList);
    void updateDaily(Long strategyId, Long dailyId, SaveDailyRequestDto requestDto);
    void deleteDaily(Long strategyId, Long dailyId);
    PageResponse<DailyResponseDto> findDaily(Long strategyId, int page, LocalDateTime startDate, LocalDateTime endDate);
}
