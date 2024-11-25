package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description="매매 방식 조회 응답 객체")
public class MethodGetResponseDto {

    @Schema(description="매매 방식 Id", example="1")
    private Long id;

    @Schema(description="매매 방식 이름", example="")
    private String name;

//    추후 추가 예정
//    @Schema(description = "아이콘 이미지 경로")
//    private String filePath;
}
