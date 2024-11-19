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

    /**
     * 다운로드 공통 사항 : presignedUrl 제공
     * @param fileRequestDto ReferenceType(참조 테이블명)과 ReferenceId(참조 id) 입력
     */
    public String getFilePath(FileRequestDto fileRequestDto);
    public Map<Long, FileResponseDto> getFileResponseByReferenceId(FileRequestDto fileRequestDto);
//    public List<FileResponseDto> getFileResponse(FileRequestDto fileRequestDto);


    public String updateImage(MultipartFile file, FileRequestDto fileRequestDto);
    public String updatePdf(MultipartFile file, FileRequestDto fileRequestDto);
    public String updateAnyFile(MultipartFile file, FileRequestDto fileRequestDto);
    public boolean deleteFile(FileRequestDto fileRequestDto);
}
