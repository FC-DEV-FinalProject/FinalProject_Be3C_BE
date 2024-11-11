package com.be3c.sysmetic.global.util.file.service;

import com.be3c.sysmetic.global.util.file.dto.FileResponseDto;
import com.be3c.sysmetic.global.util.file.dto.FileRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileService {
    /**
     * 업로드 공통 사항
     * @param file 업로드 할 파일
     * @param fileRequestDto ReferenceType(테이블명)과 ReferenceId 입력
     * @return 파일 다운로드 url? 현재는 s3 keyName
     */
    String uploadImage(MultipartFile file, FileRequestDto fileRequestDto);
    String uploadPdf(MultipartFile file, FileRequestDto fileRequestDto);
    String uploadAnyFile(MultipartFile file, FileRequestDto fileRequestDto);
}
