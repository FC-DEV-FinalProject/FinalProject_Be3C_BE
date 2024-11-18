package com.be3c.sysmetic.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderPutRequestDto {
    @NotBlank
    private Long folderId;

    @NotBlank
    @Size(min = 3, max = 20)
    private String folderName;

    @NotBlank
    private Boolean checkDupl;
}
