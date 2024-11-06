package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodGetResponseDto {
    private Long id;
    private String name;
    private String file_path;
}
