package com.be3c.sysmetic.domain.strategy.dto;

import com.be3c.sysmetic.domain.strategy.validation.ValidCycle;
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
public class SaveStrategyRequestDto {

    @NotNull
    private Long traderId;

    @NotNull
    private Long methodId;

    @NotEmpty
    private List<Long> stockIdList;

    @Length(max = 30)
    @NotBlank
    private String name;

    @NotBlank
    private String content;

    @ValidCycle
    @NotNull
    private Character cycle;

    // @DecimalMin("0.0")
    // @NotNull
    // private Double minOperationAmount;
}
