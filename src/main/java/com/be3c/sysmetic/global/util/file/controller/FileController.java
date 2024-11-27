package com.be3c.sysmetic.global.util.file.controller;

import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequestDto;
import com.be3c.sysmetic.global.util.file.dto.FileResponseDto;
import com.be3c.sysmetic.global.util.file.service.FileServiceImpl;
import io.swagger.v3.oas.annotations.Hidden;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 테스트용
 */

@Hidden
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    FileServiceImpl fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws FileUploadException {
        if (file.isEmpty()) {
            throw new FileUploadException("파일이 비어 있습니다.");
        }

        FileRequestDto fileRequestDto = new FileRequestDto(FileReferenceType.STRATEGY, 110000L);
        fileService.uploadImage(file, fileRequestDto);
        return ResponseEntity.ok("파일 업로드 성공");

//        fileService.uploadFile(file);
        // 임시, 파일 저장 경로
//        String uploadDir = "C:\\Users\\user\\IdeaProjects\\FinalProject_Be3C_BE\\src\\test\\resources\\file\\";
//        File dest = new File(uploadDir + file.getOriginalFilename());
//        try {
//            // 파일 저장
//            file.transferTo(dest);
//            return "파일 업로드 성공: " + dest.getAbsolutePath();
//        } catch (IOException e) {
//            return "파일 업로드 실패: " + e.getMessage();
//        }

    }

    @GetMapping("/url")
    public ResponseEntity<?> getUrl(@RequestParam("referenceType") String referenceType,
                                    @RequestParam("referenceId") Long referenceId){

        FileReferenceType fileReferenceType = FileReferenceType.valueOf(referenceType.toUpperCase());
        // IllegalArgumentException

        FileRequestDto fileRequestDto = new FileRequestDto(fileReferenceType, referenceId);
        Map<Long, FileResponseDto> fileMap = fileService.getFileResponseByReferenceId(fileRequestDto);

        return ResponseEntity.ok(fileMap.get(referenceId).url());
    }

}
