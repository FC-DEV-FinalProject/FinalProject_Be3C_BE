package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매매 유형 등록 요청 객체")
public class MethodPostRequestDto {

    @JsonProperty("name")
    @Schema(description = "매매 유형명", example = "Manual")
    @NotBlank
    private String name;

    @JsonProperty("checkDupl")
    @Schema(description = "매매 유형명 중복 체크 여부", example = "true")
    @NotNull
    private Boolean checkDupl;
}
