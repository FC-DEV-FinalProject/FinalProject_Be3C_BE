package com.be3c.sysmetic.domain.member.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FolderListResponseDto {
    Long id;
    String name;
    LocalDateTime latestInterestStrategyAddedDate;
}
