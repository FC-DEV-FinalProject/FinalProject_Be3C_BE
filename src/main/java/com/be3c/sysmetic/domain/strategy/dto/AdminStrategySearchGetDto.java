package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "")
public class AdminStrategySearchGetDto {
    private String openStatus;
    private String approvalStatus;
    private String keyword;
    private Integer page;
}
