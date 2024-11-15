package com.be3c.sysmetic.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderPostRequestDto {
    @NotBlank
    @Size(min = 3, max = 20)
    private String name;

    @NotBlank
    private Boolean CheckDupl;
}
