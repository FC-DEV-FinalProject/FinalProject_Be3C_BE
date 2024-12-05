package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 삭제 요청 정보를 담는 DTO")
public class ReplyDeleteRequestDto {

    @Schema(description = "전략 ID", example = "12345")
    @NotNull(message = "strategyId는 필수 값입니다.")
    @JsonProperty("strategyId")
    private Long strategyId;

    @Schema(description = "댓글 ID", example = "67890")
    @NotNull(message = "id는 필수 값입니다.")
    @JsonProperty("replyId")
    private Long id;
}
