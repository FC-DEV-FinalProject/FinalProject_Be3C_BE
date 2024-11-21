package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockPostRequestDto {
    @NotBlank
    @JsonProperty("name")
    private String name;

    @NotBlank
    @JsonProperty("checkDuplicate")
    private Boolean checkDuplicate;

    @NotNull
    private MultipartFile stockImage;
}
