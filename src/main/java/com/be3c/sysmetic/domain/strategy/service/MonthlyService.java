package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.MonthlyGetResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.Monthly;
import com.be3c.sysmetic.global.common.response.PageResponse;

import java.time.LocalDate;
import java.util.List;

public interface MonthlyService {
    void updateMonthly(Long strategyId, List<LocalDate> updatedDateList);
    PageResponse<MonthlyGetResponseDto> findMonthly(Long strategyId, Integer page, String startYearMonth, String endYearMonth);
    Monthly calculateMonthlyData(Long strategyId, int year, int month);
    PageResponse<MonthlyGetResponseDto> findTraderMonthly(Long strategyId, Integer page, String startYearMonth, String endYearMonth);
}
