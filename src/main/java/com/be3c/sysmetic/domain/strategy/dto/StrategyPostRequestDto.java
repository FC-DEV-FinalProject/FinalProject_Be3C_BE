package com.be3c.sysmetic.domain.strategy.dto;

import com.be3c.sysmetic.domain.strategy.validation.ValidCycle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StrategyPostRequestDto {

    // todo. security 적용 후 제거 필요
    @Schema(description = "트레이더 식별번호", example = "1")
    private Long traderId;

    @Schema(description = "매매방식 식별번호", example = "1")
    @NotNull
    private Long methodId;

    @Schema(description = "종목 식별번호 리스트", example = "[1, 2]")
    @NotEmpty
    private List<Long> stockIdList;

    @Schema(description = "전략명", example = "테스트 전략")
    @Length(max = 30)
    @NotBlank
    private String name;

    @Schema(description = "전략 내용", example = "테스트 전략입니다.")
    @NotBlank
    private String content;

    @Schema(description = "주기 - D 또는 P", example = "P")
    @ValidCycle
    @NotNull
    private Character cycle;

}
