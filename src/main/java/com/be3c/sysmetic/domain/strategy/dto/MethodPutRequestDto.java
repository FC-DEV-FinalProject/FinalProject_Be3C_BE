package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매매 유형 수정 요청 객체")
public class MethodPutRequestDto {

    @Schema(description = "매매 유형 PK", example = "1")
    @NotBlank
    private Long id;

    @Schema(description = "매매 유형 수정할 이름", example = "FullAuto")
    @NotBlank
    private String name;

    @Schema(description = "매매 유형명 중복 체크 여부", example = "true")
    @NotBlank
    private Boolean checkDuplicate;

//    @Schema(description = "매매 유형 아이콘 파일")
//    @NotBlank
//    private MultipartFile filePath;
}
