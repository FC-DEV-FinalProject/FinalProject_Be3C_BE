package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.DailyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.DailyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.DailyPostResponseDto;
import com.be3c.sysmetic.global.common.response.PageResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface DailyService {
    void saveDaily(Long strategyId, List<DailyPostRequestDto> requestDtoList);
    DailyPostResponseDto getIsDuplicate(Long strategyId, List<DailyPostRequestDto> requestDtoList);
    void updateDaily(Long strategyId, Long dailyId, DailyPostRequestDto requestDto);
    void deleteDaily(Long strategyId, Long dailyId);
    PageResponse<DailyGetResponseDto> findDaily(Long strategyId, int page, LocalDateTime startDate, LocalDateTime endDate);
}
