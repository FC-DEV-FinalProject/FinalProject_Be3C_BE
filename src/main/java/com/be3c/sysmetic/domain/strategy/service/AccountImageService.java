package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.AccountImageResponseDto;
import org.springframework.data.domain.Page;

public interface AccountImageService {
    Page<AccountImageResponseDto> findAccountImage(Long strategyId, int page);
    void deleteAccountImage(Long accountImageId);
    void saveAccountImage(Long strategyId, String title);
}