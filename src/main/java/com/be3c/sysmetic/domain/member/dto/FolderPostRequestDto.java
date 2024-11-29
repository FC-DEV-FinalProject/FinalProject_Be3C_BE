package com.be3c.sysmetic.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "폴더 생성 요청 DTO")
public class FolderPostRequestDto {

    @NotBlank
    @Size(min = 3, max = 20)
    @Schema(description = "폴더 이름 (3~20자)", example = "새로운 폴더")
    @JsonProperty("name")
    private String name;

    @NotNull
    @Schema(description = "중복 체크 여부", example = "true")
    @JsonProperty("checkDupl")
    private Boolean checkDupl;
}
