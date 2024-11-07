package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockPutRequestDto {
    Long id;
    String name;
    Boolean checkDuplicate;
    MultipartFile file;
}
