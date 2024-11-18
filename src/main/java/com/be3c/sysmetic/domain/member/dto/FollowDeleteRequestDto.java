package com.be3c.sysmetic.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowDeleteRequestDto {
    @NotBlank
    private List<Long> strategyId;
}
