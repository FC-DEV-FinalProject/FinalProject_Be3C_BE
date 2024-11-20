package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StockGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StockPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StockPutRequestDto;
import com.be3c.sysmetic.global.common.response.PageResponse;

public interface StockService {
    StockGetResponseDto findItemById(Long id);
    boolean duplCheck(String name);
    PageResponse<StockGetResponseDto> findItemPage(Integer page);

    boolean saveItem(StockPostRequestDto requestDto);
    boolean updateItem(StockPutRequestDto requestDto);
    boolean deleteItem(Long id);
}
