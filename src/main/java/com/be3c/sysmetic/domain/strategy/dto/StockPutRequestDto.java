package com.be3c.sysmetic.domain.strategy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockPutRequestDto {
    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private Boolean checkDuplicate;
//    @NotNull
    // private MultipartFile file;
}
