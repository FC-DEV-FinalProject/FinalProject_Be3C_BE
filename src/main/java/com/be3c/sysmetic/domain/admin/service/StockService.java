package com.be3c.sysmetic.domain.admin.service;

import com.be3c.sysmetic.domain.admin.dto.StockPutRequestDto;
import com.be3c.sysmetic.domain.admin.entity.Stock;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface StockService {
    Stock findItemById(Long id);
    boolean duplcheck(String name);
    Object findItemIcon(Long itemId);
    Page<Stock> findItemPage(Integer page);

    boolean saveItem(StockPutRequestDto requestDto, Long userId);
    boolean updateItem(StockPutRequestDto requestDto, Long userId);
    boolean deleteItem(Long id, Long userId);
}
