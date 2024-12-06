package com.be3c.sysmetic.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "폴더 조회 요청 DTO", example = "{ \"folderId\": 123, \"page\": 1 }")
public class FolderGetRequestDto {

    @Schema(description = "폴더 ID", example = "123")
    @NotNull(message = "폴더 ID는 필수 값입니다.")
    private Long folderId;

    @Schema(description = "페이지 번호", example = "1")
    @NotNull(message = "페이지 번호는 필수 값입니다.")
    private Integer page;
}