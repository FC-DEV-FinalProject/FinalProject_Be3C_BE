package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "종목 등록 요청 DTO")
public class StockPostRequestDto {

    @NotBlank
    @JsonProperty("name")
    @Schema(description = "종목 이름", example = "삼성전자", required = true)
    private String name;

    @NotNull
    @JsonProperty("checkDuplicate")
    @Schema(description = "중복 체크 여부", example = "true", required = true)
    private Boolean checkDuplicate;

    // MultipartFile file;
}
