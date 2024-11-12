package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodPostRequestDto {
    private String name;
    private Boolean checkDuplicate;
    //private MultipartFile filePath;
}
