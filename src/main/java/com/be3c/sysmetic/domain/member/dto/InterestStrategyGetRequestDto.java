package com.be3c.sysmetic.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestStrategyGetRequestDto {
    @NotBlank
    private Long folderId;

    @NotBlank
    private Integer page;
}
