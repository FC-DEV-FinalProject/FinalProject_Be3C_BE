package com.be3c.sysmetic.global.util.file.repository;

import com.be3c.sysmetic.global.util.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long>, FileRepositoryCustom {}