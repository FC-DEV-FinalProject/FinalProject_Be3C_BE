package com.be3c.sysmetic.domain.strategy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
public class StrategyListResponseDto<Strategy> {      // 전략 (랭킹) 목록에서 필요한 정보를 DTO로 만들어서 반환

    @NotEmpty
    private List<Page> strategyList;

    @NotBlank
    private Long strategyId;

    @NotBlank
    private Long trader;

    @NotBlank
    @Size(min = 1, max = 30)
    private String name;

    @NotNull
    private Double accumProfitRate;

    @NotNull
    private Double kpRatio;

    @NotNull
    private Double smScore;
}