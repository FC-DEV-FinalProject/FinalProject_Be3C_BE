package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AccountImageResponseDto {

    private Long accountImageId;

    private String title;

    private String imageUrl;

    private LocalDateTime createdAt;
}
