package com.be3c.sysmetic.domain.member.dto.inquiry;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InquirySavePageShowRequestDto {

    @Schema(description = "전략 ID", example = "12345")
    private String strategyId;
}