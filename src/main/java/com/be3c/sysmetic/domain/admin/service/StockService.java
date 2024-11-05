package com.be3c.sysmetic.domain.admin.service;

import com.be3c.sysmetic.domain.admin.dto.StockRequestDto;
import com.be3c.sysmetic.domain.admin.entity.Stock;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface StockService {
    Optional<Stock> findItemById(Long id);
    Optional<Stock> findItemByName(String name);
    Optional<Object> findItemIcon(Long itemId);
    Page<List<Stock>> findItemPage(Integer page);

    boolean saveItem(StockRequestDto requestDto, Long userId);
    boolean updateItem(StockRequestDto requestDto);
}
