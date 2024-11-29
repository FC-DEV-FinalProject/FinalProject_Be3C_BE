package com.be3c.sysmetic.global.util.file.repository;

import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    @Query("SELECT f " +
            "FROM File f " +
            "WHERE f.referenceId = :#{#fileRequest.referenceId} " +
            "AND f.referenceType = :#{#fileRequest.referenceType} " +
            "ORDER BY f.referenceId")
    List<File> findFilesByFileReference(@Param("fileRequest") FileRequest fileRequest);
}
