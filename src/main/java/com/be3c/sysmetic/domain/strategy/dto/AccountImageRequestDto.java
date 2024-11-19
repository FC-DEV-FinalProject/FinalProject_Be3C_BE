package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AccountImageRequestDto {

    @Schema(description = "실계좌 이미지")
    @NotNull
    private MultipartFile image;

    @Schema(description = "실계좌 이미지 제목", example = "2024-11-19-계좌")
    @NotNull
    private String title;

}
