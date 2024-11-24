package com.be3c.sysmetic.global.util.file.entity;

import com.be3c.sysmetic.global.entity.BaseEntity;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Builder @Getter
@NoArgsConstructor
@AllArgsConstructor
public class File extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;
    private String type;
    private long size;
    private String originalName;

    private FileReferenceType referenceType;
    private Long referenceId;
    private boolean isDeleted;
    private LocalDate expireDate;
}
