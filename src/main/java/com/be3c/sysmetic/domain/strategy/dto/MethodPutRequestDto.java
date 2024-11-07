package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodPutRequestDto {
    Long id;
    String name;
    Boolean duplCheck;
    MultipartFile filePath;
}