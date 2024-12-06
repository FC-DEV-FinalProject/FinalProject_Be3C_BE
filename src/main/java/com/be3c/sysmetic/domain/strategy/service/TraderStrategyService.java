package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.*;
import com.be3c.sysmetic.global.common.response.PageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface TraderStrategyService {
    MyStrategyListResponseDto getMyStrategyList(Integer page);
    StrategyPostResponseDto insertStrategy(StrategyPostRequestDto requestDto, MultipartFile file);
    void updateStrategy(Long strategyId, StrategyPostRequestDto requestDto, MultipartFile file);
    void deleteStrategy(StrategyDeleteRequestDto requestDto);
}
