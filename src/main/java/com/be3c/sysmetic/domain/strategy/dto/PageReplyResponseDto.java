package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 응답 정보를 담는 DTO")
public class PageReplyResponseDto {

    @Schema(description = "전략 ID", example = "12345")
    @JsonProperty("strategy_id")
    private Long strategyId;

    @Schema(description = "회원 ID", example = "67890")
    @JsonProperty("member_id")
    private Long memberId;

    @Schema(description = "댓글 내용", example = "이 전략 정말 유용하네요!")
    @JsonProperty("content")
    private String content;
}
