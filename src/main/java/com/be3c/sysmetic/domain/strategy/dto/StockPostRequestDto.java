package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockPostRequestDto {
    String name;
    Boolean checkDuplicate;
    MultipartFile file;
}
