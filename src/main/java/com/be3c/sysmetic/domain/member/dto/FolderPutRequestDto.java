package com.be3c.sysmetic.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "폴더 수정 요청 DTO")
public class FolderPutRequestDto {

    @NotNull
    @JsonProperty("folderId")
    @Schema(description = "폴더 ID", example = "1")
    private Long folderId;

    @NotBlank
    @Size(min = 3, max = 20)
    @JsonProperty("folderName")
    @Schema(description = "폴더 이름 (3~20자)", example = "업데이트된 폴더 이름")
    private String folderName;

    @NotNull
    @JsonProperty("checkDupl")
    @Schema(description = "중복 체크 여부", example = "true")
    private Boolean checkDupl;
}
