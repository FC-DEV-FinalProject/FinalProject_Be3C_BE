package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StockPutRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import org.springframework.data.domain.Page;

public interface StockService {
    Stock findItemById(Long id);
    boolean duplcheck(String name);
    Object findItemIcon(Long itemId);
    Page<Stock> findItemPage(Integer page);

    boolean saveItem(StockPutRequestDto requestDto);
    boolean updateItem(StockPutRequestDto requestDto);
    boolean deleteItem(Long id, Long userId);
}
