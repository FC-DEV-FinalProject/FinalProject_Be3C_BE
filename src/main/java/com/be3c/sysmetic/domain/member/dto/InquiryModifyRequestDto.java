package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 문의 수정 요청 DTO")
public class InquiryModifyRequestDto {

    @Schema(description = "문의 제목", example = "Updated Strategy Inquiry Title")
    @NotNull
    private String inquiryTitle;

    @Schema(description = "문의 내용", example = "Updated content for the strategy inquiry.")
    @NotNull
    private String inquiryContent;
}