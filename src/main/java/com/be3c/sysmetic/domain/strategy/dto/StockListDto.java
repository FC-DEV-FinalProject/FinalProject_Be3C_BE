package com.be3c.sysmetic.domain.strategy.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;

@Getter
@Builder
public class StockListDto {

    /*
        StockListDto : 종목 리스트 응답용 Dto

        id : StrategyStockReference id
        stockNames : 종목명
    */

    private HashSet<Long> stockIds;
    private HashSet<String> stockNames;
}