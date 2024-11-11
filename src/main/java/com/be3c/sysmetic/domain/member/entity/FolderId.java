package com.be3c.sysmetic.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class FolderId implements Serializable {

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "folder_id")
    private Long folderId;
}
