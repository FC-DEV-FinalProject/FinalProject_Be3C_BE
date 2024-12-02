package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AccountImageDeleteRequestDto {

    @Schema(description = "삭제할 실계좌 이미지 id 목록")
    @NotBlank
    List<Long> accountImageId;

}
