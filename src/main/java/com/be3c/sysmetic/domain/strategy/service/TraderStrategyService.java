package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StrategyPostRequestDto;
import org.springframework.web.multipart.MultipartFile;

public interface TraderStrategyService {
    void insertStrategy(StrategyPostRequestDto requestDto, MultipartFile file);
    void updateStrategy(Long strategyId, StrategyPostRequestDto requestDto, MultipartFile file);
    void deleteStrategy(Long strategyId);
}
