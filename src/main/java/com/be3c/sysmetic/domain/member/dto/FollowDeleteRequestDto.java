package com.be3c.sysmetic.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "팔로우 삭제 요청 DTO")
public class FollowDeleteRequestDto {

    @NotBlank
    @Schema(description = "삭제할 전략 ID 리스트", example = "[1, 2, 3]")
    private List<Long> strategyId;
}
