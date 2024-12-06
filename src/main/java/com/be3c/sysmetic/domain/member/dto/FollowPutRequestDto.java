package com.be3c.sysmetic.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "폴더 이동 요청 DTO")
public class FollowPutRequestDto {

    @JsonProperty("originFolderId")
    @Schema(description = "원래 폴더의 ID", example = "1")
    @NotNull
    private Long originFolderId;

    @JsonProperty("toFolderId")
    @Schema(description = "이동할 폴더의 ID", example = "2")
    @NotNull
    private Long toFolderId;

    @JsonProperty("strategyId")
    @Schema(description = "이동할 전략의 ID", example = "10")
    @NotNull
    private Long strategyId;
}