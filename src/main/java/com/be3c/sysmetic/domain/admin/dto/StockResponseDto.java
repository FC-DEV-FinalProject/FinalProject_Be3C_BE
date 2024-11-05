package com.be3c.sysmetic.domain.admin.dto;

import com.be3c.sysmetic.domain.admin.entity.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class StockResponseDto {
    private Stock stock;
    private String filepath;
}
