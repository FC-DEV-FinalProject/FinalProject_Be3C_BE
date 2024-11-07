package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodPostRequestDto {
    String name;
    Boolean duplCheck;
    String filePath;
}
