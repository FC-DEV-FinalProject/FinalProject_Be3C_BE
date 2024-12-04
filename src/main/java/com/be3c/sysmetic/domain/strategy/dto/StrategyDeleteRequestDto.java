package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "전략 삭제 요청 DTO")
public class StrategyDeleteRequestDto {
    @JsonProperty("idList")
    List<Long> strategyIdList;
}
