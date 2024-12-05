package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StockDeleteRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StockGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StockPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StockPutRequestDto;
import com.be3c.sysmetic.global.common.response.PageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface StockService {
    StockGetResponseDto findItemById(Long id);
    boolean duplCheck(String name);
    PageResponse<StockGetResponseDto> findItemPage(Integer page);

    boolean saveItem(StockPostRequestDto requestDto, MultipartFile file);
    boolean updateItem(StockPutRequestDto requestDto, MultipartFile file);
    Map<Long, String> deleteItem(StockDeleteRequestDto idList);
}
