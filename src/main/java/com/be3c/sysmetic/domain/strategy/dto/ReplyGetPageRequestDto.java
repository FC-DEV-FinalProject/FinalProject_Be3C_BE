package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 페이지 요청 정보를 담는 DTO")
public class ReplyGetPageRequestDto {

    @Schema(description = "전략 ID", example = "12345")
    @NotNull(message = "strategyId는 필수 값입니다.")
    @JsonProperty("strategyId")
    private Long strategyId;

    @Schema(description = "페이지 번호", example = "1")
    @NotNull(message = "page는 필수 값입니다.")
    @Min(value = 1, message = "page는 1 이상이어야 합니다.")
    @JsonProperty("page")
    private Integer page;
}
