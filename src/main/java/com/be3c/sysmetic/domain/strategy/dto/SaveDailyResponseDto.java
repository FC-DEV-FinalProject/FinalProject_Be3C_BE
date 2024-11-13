package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SaveDailyResponseDto {
    private boolean isDuplicate;
}
