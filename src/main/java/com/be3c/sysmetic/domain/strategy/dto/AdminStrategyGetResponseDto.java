package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "")
public class AdminStrategyGetResponseDto {
    private Long strategyId;
    private String strategyName;
    private String traderName;
    private String openStatusCode;
    private String ApprovalStatusCode;
    private LocalDateTime strategyCreateDate;
    private List<String> iconFilePath;
}
