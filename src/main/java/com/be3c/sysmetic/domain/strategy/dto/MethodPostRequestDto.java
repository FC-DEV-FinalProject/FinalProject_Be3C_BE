package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매매 유형 등록 요청 객체")
public class MethodPostRequestDto {
    @Schema(description = "매매 유형명", example = "Manual")
    @NotNull
    private String name;

    @Schema(description = "매매 유형명 중복 체크 여부", example = "true")
    @NotNull
    private Boolean checkDuplicate;

//    @Schema(description = "매매 유형 아이콘 파일")
//    private MultipartFile filePath;
}
