package com.be3c.sysmetic.global.util.file.service;

import com.be3c.sysmetic.global.util.file.dto.FileResponseDto;
import com.be3c.sysmetic.global.util.file.dto.FileRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileService {
    /**
     * 이미지 업로드
     * @param file 업로드 할 이미지 파일
     * @param fileRequestDto ReferenceType과 ReferenceId 입력
     * @return s3 keyName
     */
    String uploadImage(MultipartFile file, FileRequestDto fileRequestDto);
    /**
     * PDF 업로드
     * @param file 업로드 할 PDF 파일
     * @param fileRequestDto ReferenceType과 ReferenceId 입력
     * @return s3 keyName
     */

    String uploadPdf(MultipartFile file, FileRequestDto fileRequestDto);
    /**
     * 확장자 무제한 업로드
     * @param file 업로드 할 파일
     * @param fileRequestDto ReferenceType과 ReferenceId 입력
     * @return s3 keyName
     */
    String uploadAnyFile(MultipartFile file, FileRequestDto fileRequestDto);



    /**
     * presignedUrl 제공
     * @param fileRequestDto ReferenceType(참조 테이블명)과 ReferenceId(참조 id) 입력
     * @return 파일 url
     */
    String getFilePath(FileRequestDto fileRequestDto);
    /**
     * presignedUrl과 파일 정보를 제공
     * @param fileRequestDto ReferenceType(참조 테이블명)과 ReferenceId(참조 id) 입력
     * @return url과 originalName을 FileResponseDto로 반환
     */
    Map<Long, FileResponseDto> getFileResponseByReferenceId(FileRequestDto fileRequestDto);
//    public List<FileResponseDto> getFileResponse(FileRequestDto fileRequestDto);



    /**
     * 이미지 수정
     * @param file 수정된 파일
     * @param fileRequestDto 수정할 위치: ReferenceType, ReferenceId
     * @return s3 keyName
     */
    String updateImage(MultipartFile file, FileRequestDto fileRequestDto);
    /**
     * pdf 수정
     * @param file 수정된 파일
     * @param fileRequestDto 수정할 위치: ReferenceType, ReferenceId
     * @return s3 keyName
     */
    String updatePdf(MultipartFile file, FileRequestDto fileRequestDto);
    /**
     * 파일 수정
     * @param file 수정된 파일
     * @param fileRequestDto 수정할 위치: ReferenceType, ReferenceId
     * @return s3 keyName
     */
    String updateAnyFile(MultipartFile file, FileRequestDto fileRequestDto);
    /**
     * 파일 삭제
     * @param fileRequestDto 삭제할 파일:  ReferenceType, ReferenceId
     * @return 삭제 여부
     */
    boolean deleteFile(FileRequestDto fileRequestDto);
}
