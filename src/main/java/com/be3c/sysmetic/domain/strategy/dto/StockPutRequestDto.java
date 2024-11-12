package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockPutRequestDto {
    private Long id;
    private String name;
    private Boolean checkDuplicate;
    // private MultipartFile file;
}
