package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.AccountImageRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.AccountImageResponseDto;
import com.be3c.sysmetic.global.common.response.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AccountImageService {
    PageResponse<AccountImageResponseDto> findAccountImages(Long strategyId, Integer page);
    PageResponse<AccountImageResponseDto> findTraderAccountImages(Long strategyId, Integer page);
    void deleteAccountImage(Long accountImageId);
    void saveAccountImage(Long strategyId, List<AccountImageRequestDto> requestDtos);
}