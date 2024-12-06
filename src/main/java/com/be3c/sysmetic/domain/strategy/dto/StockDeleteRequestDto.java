package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 전략 조회 응답 DTO")
public class StockDeleteRequestDto {

    @Schema(description = "stock id list")
    @JsonProperty("stockIdList")
    List<Long> stockIdList;
}
