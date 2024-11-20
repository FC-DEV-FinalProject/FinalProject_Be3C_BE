package com.be3c.sysmetic.domain.strategy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodPutRequestDto {
    @NotBlank
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private Boolean checkDuplicate;

//    private MultipartFile filePath;
}
