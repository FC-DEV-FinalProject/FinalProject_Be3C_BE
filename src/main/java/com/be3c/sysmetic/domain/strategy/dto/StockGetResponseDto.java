package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class StockGetResponseDto {
    private Long id;
    private String name;
    private String filePath;
}
