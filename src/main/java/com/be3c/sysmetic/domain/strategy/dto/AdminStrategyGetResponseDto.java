package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AdminStrategyGetResponseDto {
    private Long strategy_id;
    private String strategy_name;
    private String trader_name;
    private String status_code;
}
