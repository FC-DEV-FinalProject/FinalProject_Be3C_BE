package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "문의 등록 요청 DTO")
public class InquirySaveRequestDto {

    @Schema(description = "문의 제목", example = "전략에 대한 문의")
    @NotNull
    private String inquiryTitle;

    @Schema(description = "문의 내용", example = "이 전략에 대한 설명을 더 듣고 싶습니다.")
    @NotNull
    private String inquiryContent;
}
