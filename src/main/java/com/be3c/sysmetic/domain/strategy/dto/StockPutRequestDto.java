package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "종목 수정 요청 객체")
public class StockPutRequestDto {

    @JsonProperty("id")
    @Schema(description = "수정할 종목 PK", example = "1")
    @NotNull
    private Long id;

    @JsonProperty("name")
    @Schema(description = "수정할 종목 이름", example = "수정할 이름")
    @NotBlank
    private String name;

    @JsonProperty("checkDuplicate")
    @Schema(description = "종목 이름 중복 체크 여부", example = "true")
    @NotNull
    private Boolean checkDuplicate;
}
