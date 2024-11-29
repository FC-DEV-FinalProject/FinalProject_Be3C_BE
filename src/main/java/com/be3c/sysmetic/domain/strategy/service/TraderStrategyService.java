package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StrategyPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyPostResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface TraderStrategyService {
    StrategyPostResponseDto insertStrategy(StrategyPostRequestDto requestDto, MultipartFile file);
    void updateStrategy(Long strategyId, StrategyPostRequestDto requestDto, MultipartFile file);
    void deleteStrategy(Long strategyId);
}
