package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.MonthlyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.Monthly;
import com.be3c.sysmetic.global.common.response.PageResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface MonthlyService {
    void updateMonthly(Long strategyId, List<LocalDateTime> updatedDateList);
    PageResponse<MonthlyGetResponseDto> findMonthly(Long strategyId, Integer page, Integer startYear, Integer startMonth, Integer endYear, Integer endMonth);
    Monthly calculateMonthlyData(Long strategyId, int year, int month);
}
