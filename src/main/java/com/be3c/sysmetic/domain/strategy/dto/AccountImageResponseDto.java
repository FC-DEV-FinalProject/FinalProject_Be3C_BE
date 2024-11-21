package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AccountImageResponseDto {

    @Schema(description = "실계좌 이미지 식별번호")
    @NotNull
    private Long accountImageId;

    @Schema(description = "실계좌 이미지 제목")
    @NotBlank
    private String title;

    @Schema(description = "실계좌 이미지 파일 경로")
    @NotBlank
    private String imageUrl;

}
