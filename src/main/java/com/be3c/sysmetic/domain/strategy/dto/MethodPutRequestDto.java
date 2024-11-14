package com.be3c.sysmetic.domain.strategy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodPutRequestDto {
    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Boolean checkDuplicate;

//    @NotNull
//    private MultipartFile filePath;
}
