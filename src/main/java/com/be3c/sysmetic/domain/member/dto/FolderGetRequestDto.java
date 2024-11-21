package com.be3c.sysmetic.domain.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderGetRequestDto {
    private Long folderId;
    private Integer page;
}
