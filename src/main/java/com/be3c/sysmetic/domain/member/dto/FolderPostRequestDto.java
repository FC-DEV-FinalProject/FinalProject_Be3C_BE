package com.be3c.sysmetic.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
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
    private String name;

    @NotBlank
    @Schema(description = "중복 체크 여부", example = "true")
    private Boolean CheckDupl;
}
