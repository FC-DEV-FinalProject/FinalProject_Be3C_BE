package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockPostRequestDto {
    @JsonProperty("name")
    private String name;

    @JsonProperty("checkDuplicate")
    private Boolean checkDuplicate;
    //MultipartFile file;
}
