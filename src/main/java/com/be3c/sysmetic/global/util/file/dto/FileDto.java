package com.be3c.sysmetic.global.util.file.dto;

import java.time.LocalDate;

public record FileDto(
        Long id,
        String path,
        String type,
        long size,
        String originalName,
        String referenceType,
        Long referenceId,
        boolean isDeleted,
        LocalDate expireDate
) {
}
