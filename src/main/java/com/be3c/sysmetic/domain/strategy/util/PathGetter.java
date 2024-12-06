package com.be3c.sysmetic.domain.strategy.util;

import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.exception.FileNotFoundException;
import com.be3c.sysmetic.global.util.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathGetter {

    private final FileService fileService;

    public String getMethodIconPath(Long methodId) {
        try {
            return fileService.getFilePath(new FileRequest(FileReferenceType.METHOD, methodId));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public String getMemberProfilePath(Long memberId) {
        try {
            return fileService.getFilePath(new FileRequest(FileReferenceType.MEMBER, memberId));
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
