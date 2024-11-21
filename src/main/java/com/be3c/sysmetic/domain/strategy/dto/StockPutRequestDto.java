package com.be3c.sysmetic.domain.strategy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockPutRequestDto {
    @NotBlank
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private Boolean checkDuplicate;

    @NotNull
     private MultipartFile file;
}
