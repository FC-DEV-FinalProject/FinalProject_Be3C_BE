package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description="종목 조회 응답 객체")
public class StockGetResponseDto {
    @Schema(description = "종목 PK", example = "1")
    private Long id;

    @Schema(description = "종목명", example = "KP200옵션")
    private String name;

    @Schema(description = "종목 아이콘 이미지 경로")
    private String filePath;
}
