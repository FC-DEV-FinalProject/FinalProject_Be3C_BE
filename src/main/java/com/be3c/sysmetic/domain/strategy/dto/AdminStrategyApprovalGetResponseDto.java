package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AdminStrategyApprovalGetResponseDto {
    /*
        수정 예정
     */
    
    private Long strategy_id;
    private Long strategy_approval_id;
    private String strategy_name;
    private String trader_name;
    private String status_code;
}
