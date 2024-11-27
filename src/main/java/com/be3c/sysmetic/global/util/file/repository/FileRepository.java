package com.be3c.sysmetic.global.util.file.repository;

import com.be3c.sysmetic.global.util.file.dto.FileRequestDto;
import com.be3c.sysmetic.global.util.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    // FileReferenceDto -> referenceType, referenceId
    @Query("SELECT f " +
            "FROM File f " +
            "WHERE f.referenceId = :#{#fileRequestDto.referenceId} " +
            "AND f.referenceType = :#{#fileRequestDto.referenceType} " +
            "ORDER BY f.referenceId")
    List<File> findFilesByFileReference(@Param("fileRequestDto") FileRequest fileRequest);
}
