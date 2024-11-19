package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "")
public class StockPostRequestDto {
    @NotNull
    @JsonProperty("name")
    private String name;

    @NotNull
    @JsonProperty("checkDuplicate")
    private Boolean checkDuplicate;
    //MultipartFile file;
}
