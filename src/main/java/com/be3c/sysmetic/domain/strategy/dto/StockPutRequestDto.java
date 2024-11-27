package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "종목 수정 요청 객체")
public class StockPutRequestDto {

    @Schema(description = "수정할 종목 PK", example = "1")
    @NotBlank
    private Long id;

    @Schema(description = "수정할 종목 이름", example = "수정할 이름")
    @NotBlank
    private String name;

    @Schema(description = "종목 이름 중복 체크 여부", example = "true")
    @NotBlank
    private Boolean checkDuplicate;
}
