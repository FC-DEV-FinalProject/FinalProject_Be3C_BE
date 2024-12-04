package com.be3c.sysmetic.global.util.file.repository;

import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.entity.File;

import java.util.List;

public interface FileRepositoryCustom {
    List<File> findFilesByFileReference(FileRequest fileRequest);
}
