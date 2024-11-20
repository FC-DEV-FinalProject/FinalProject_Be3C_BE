package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "종목 등록 요청 객체")
public class StockPostRequestDto {
    @NotBlank
    @JsonProperty("name")
    private String name;

    @NotBlank
    @JsonProperty("checkDuplicate")
    private Boolean checkDuplicate;

    //MultipartFile file;
}
