package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodGetResponseDto {
    private Long id;
    private String name;
//    추후 추가 예정
//    private String file_path;
}
