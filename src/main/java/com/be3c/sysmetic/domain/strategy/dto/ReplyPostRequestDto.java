package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 작성 요청 정보를 담는 DTO")
public class ReplyPostRequestDto {

    @Schema(description = "전략 ID", example = "12345")
    @NotNull(message = "strategyId는 필수 값입니다.")
    @JsonProperty("strategyId")
    private Long strategyId;

    @Schema(description = "댓글 내용", example = "좋은 전략이네요!")
    @NotBlank(message = "content는 필수 값입니다.")
    @JsonProperty("content")
    private String content;
}
