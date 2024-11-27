package com.be3c.sysmetic.global.util.file.service;

import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.dto.FileWithInfoResponse;
import com.be3c.sysmetic.global.util.file.entity.File;
import com.be3c.sysmetic.global.util.file.exception.InvalidFileFormatException;
import com.be3c.sysmetic.global.util.file.repository.FileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    final FileRepository fileRepository;
    final S3Service s3Service;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 최대 파일 크기 : 5MB => 프로퍼티스로 옮기기?

//    @Value("${file.max-size}")
//    private long maxFileSize;
    /*
    ----------------------------------------------------------------------------------------
    upload 업로드
     */

    @Override
    public void uploadImage(MultipartFile file, FileRequest fileRequest) {

        checkFileSize(file);
        checkFileExtension(file, List.of("jpeg", "png", "gif"));

        String path = s3Service.upload(file, fileRequest);
        fileEntityBuilderAndSaver(file, path, fileRequest);

    }

    @Override
    public void uploadPdf(MultipartFile file, FileRequest fileRequest) {

        checkFileSize(file);
        checkFileExtension(file, List.of("pdf"));

        String path = s3Service.upload(file, fileRequest);
        fileEntityBuilderAndSaver(file, path, fileRequest);
    }

    @Transactional
    @Override
    public void uploadAnyFile(MultipartFile file, FileRequest fileRequest) {

        checkFileSize(file);

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isEmpty()) {
            throw new InvalidFileFormatException("파일 이름이 없습니다.");
        }

        String extension = originalName.substring(originalName.lastIndexOf('.'));
        if (extension.isEmpty() || extension.isBlank())
            throw new InvalidFileFormatException("올바른 확장자가 포함된 파일이 아닙니다.");

        String path = s3Service.upload(file, fileRequest);
        fileEntityBuilderAndSaver(file, path, fileRequest);
    }


    /**
     * 파일 정보를 DB에 저장하는 메소드
     *
     * @param file        업로드 할 파일
     * @param s3KeyName   S3에 저장된 path
     * @param fileRequest 파일을 사용할 곳의 정보
     *                    referenceType 참조할 테이블명 referenceId 참조id
     */
    private void fileEntityBuilderAndSaver(MultipartFile file, String s3KeyName, FileRequest fileRequest) {
        try {
            File fileEntity = File.builder()
                    .path(s3KeyName)
                    .type(file.getContentType())
                    .size(file.getSize())
                    .originalName(file.getOriginalFilename())
                    .referenceType(fileRequest.referenceType())
                    .referenceId(fileRequest.referenceId())
                    .build();
            fileRepository.save(fileEntity);
        } catch (Exception e) {
            s3Service.deleteObject(s3KeyName);
            log.error("파일 정보 업로드에 실패하였습니다. S3 키: {}. 파일 이름: {}.", s3KeyName, file.getOriginalFilename(), e);
            throw new RuntimeException("파일 정보 업로드에 실패하였습니다", e);
        }
    }

    /*

    upload 업로드
    ------------------------------------------------------------------------------------------------
    download 다운로드

     */


    @Override
    public String getFilePath(FileRequest fileRequest) {

        List<File> files = fileRepository.findFilesByFileReference(fileRequest);
        if (files.isEmpty()) {
            log.error("파일을 찾을 수 없습니다. 파일 참조 정보: {}", fileRequest);
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        }

        File file = files.get(0);
        // size 2 이상일 시 로깅하기

        return s3Service.createPresignedGetUrl(file.getPath());
    }

    @Override
    public List<String> getFilePaths(FileRequest fileRequest) {

        List<File> files = fileRepository.findFilesByFileReference(fileRequest);
        if (files.isEmpty()) {
            log.error("파일을 찾을 수 없습니다. 파일 참조 정보: {}", fileRequest);
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        }

        List<String> paths = new ArrayList<>(files.size());
        for (File f : files)
            paths.add(s3Service.createPresignedGetUrl(f.getPath()));

        return paths;

    }

    @Override
    public List<FileWithInfoResponse> getFileWithInfos(FileRequest fileRequest) {

        List<File> files = fileRepository.findFilesByFileReference(fileRequest);
        if (files.isEmpty()) {
            log.error("파일을 찾을 수 없습니다. 파일 참조 정보: {}", fileRequest);
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        }

        List<FileWithInfoResponse> fileWithInfoResponses = new ArrayList<>();

        for (File f : files) {
            String url = s3Service.createPresignedGetUrl(f.getPath());
            FileWithInfoResponse fileWithInfoResponse = new FileWithInfoResponse(f.getId(), url, f.getOriginalName(), f.getSize());
            fileWithInfoResponses.add(fileWithInfoResponse);
        }

        return fileWithInfoResponses;
    }



  /*
  download
  ------------------------------------------------------------------------------
  update
   */


    @Transactional
    @Override
    public void updateImage(MultipartFile file, FileRequest fileRequest) {

        checkFileSize(file);
        checkFileExtension(file, List.of("jpeg", "png", "gif"));

        String path = s3Service.upload(file, fileRequest);
        fileEntityBuilderAndSaver(file, path, fileRequest);
    }

    @Transactional
    @Override
    public void updatePdf(MultipartFile file, FileRequest fileRequest) {

        checkFileSize(file);
        checkFileExtension(file, List.of("pdf"));

        String path = s3Service.upload(file, fileRequest);
        fileEntityBuilderAndSaver(file, path, fileRequest);
    }

    @Override
    public void updateAnyFile(MultipartFile file, FileRequest fileRequest) {

        checkFileSize(file);

        String path = s3Service.upload(file, fileRequest);
        fileEntityBuilderAndSaver(file, path, fileRequest);
    }

    @Override
    public void updateImageById(MultipartFile file, Long updateTargetId) {

        checkFileSize(file);
        checkFileExtension(file, List.of("jpeg", "png", "gif"));
        updateFile(file, updateTargetId);
    }

    @Override
    public void updatePdfById(MultipartFile file, Long updateTargetId) {

        checkFileSize(file);
        checkFileExtension(file, List.of("pdf"));
        updateFile(file, updateTargetId);
    }

    @Override
    public void updateAnyFileById(MultipartFile file, Long updateTargetId) {

        checkFileSize(file);
        updateFile(file, updateTargetId);
    }

    /**
     * 파일 업데이트 로직
     *
     * @param file     업로드할 파일
     * @param targetId 업데이트할 파일 ID
     */
    private void updateFile(MultipartFile file, Long targetId) {

        Optional<File> existing = fileRepository.findById(targetId);
        if (existing.isEmpty()) {
            log.error("파일을 찾을 수 없습니다. 업데이트하려는 파일 ID: {}", targetId);
            throw new EntityNotFoundException("파일을 찾을 수 없습니다.");
        }

        fileRepository.save(File.builder()
                .id(targetId)
                .size(file.getSize())
                .originalName(file.getOriginalFilename())
                .type(file.getContentType())
                .build());
        try {
            s3Service.updateObject(file, existing.get().getPath());
        } catch (Exception e) {
            fileRepository.save(existing.get());
        }
    }


  /*
  update
  ------------------------------------------------------------------------------
  delete
  */

    @Override
    public boolean deleteFile(FileRequest fileRequest) {

        List<File> file = fileRepository.findFilesByFileReference(fileRequest);
        if (file.isEmpty()) {
            log.error("삭제하려는 파일을 찾을 수 없습니다. 파일 참조 정보: {}", fileRequest);
            throw new IllegalArgumentException("삭제할 파일을 찾을 수 없습니다.");
        }
        fileRepository.delete(file.get(0));

        try {
            fileRepository.delete(file.get(0));
            return s3Service.deleteObject(file.get(0).getPath());
        } catch (Exception e) {
            log.error("파일 삭제 실패. 파일 ID: {}, 파일 경로: {}", file.get(0).getId(), file.get(0).getPath(), e);
            throw new RuntimeException("파일 삭제 실패");
        }
    }

    @Override
    public boolean deleteFiles(FileRequest fileRequest) {

        List<File> files = fileRepository.findFilesByFileReference(fileRequest);

        if (files.isEmpty()) {
            log.error("삭제하려는 파일을 찾을 수 없습니다. 파일 참조 정보: {}", fileRequest);
            throw new IllegalArgumentException("삭제할 파일을 찾을 수 없습니다.");
        }

        try {
            for (File file : files) {
                s3Service.deleteObject(file.getPath());
                fileRepository.delete(file);
            }
        } catch (Exception e) {
            log.error("파일 삭제 실패. 파일 참조 정보: {}", fileRequest, e);
            throw new RuntimeException("파일 삭제 실패");
        }

        return true;
    }

    @Override
    public boolean deleteFileById(Long fileId) {
        Optional<File> file = fileRepository.findById(fileId);
        if (file.isEmpty()) {
            log.error("삭제하려는 파일을 찾을 수 없습니다. 파일 id: {}", fileId);
            throw new IllegalArgumentException("삭제할 파일을 찾을 수 없습니다.");
        }

        try {
            s3Service.deleteObject(file.get().getPath());
            fileRepository.delete(file.get());
        } catch (Exception e) {
            log.error("파일 삭제 실패. 파일 id: {}", fileId, e);
            throw new RuntimeException("파일 삭제 실패");
        }
        return true;
    }

/*
  delete
  ------------------------------------------------------------------------------
  common
  */

    /**
     * 파일 존재, 크기 체크
     *
     * @param file 체크할 파일
     */
    private void checkFileSize(MultipartFile file) {
        if (file.isEmpty()) {
            log.error("업로드된 파일이 비어있습니다. 파일 이름: {}", file.getOriginalFilename());
            throw new InvalidFileFormatException("파일이 비어있습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            log.error("파일 크기가 너무 큽니다. 파일 이름: {}, 크기: {}MB, 최대 크기: {}MB",
                    file.getOriginalFilename(), file.getSize() / 1024 / 1024, MAX_FILE_SIZE / 1024 / 1024);
            throw new InvalidFileFormatException("파일 크기가 너무 큽니다. 최대 크기는 " + MAX_FILE_SIZE / 1024 / 1024 + "MB입니다.");
        }
    }

    /**
     * 파일의 타입을 검사하는 메서드
     *
     * @param file                검사할 파일
     * @param allowedContentTypes 허용된 파일 타입들
     * @return 검사된 컨텐트 타입
     * @throws InvalidFileFormatException 파일 형식이 맞지 않으면 예외 발생
     */
    private String checkFileExtension(MultipartFile file, List<String> allowedContentTypes) {
        String type = file.getContentType();
        for (String allowedContentType : allowedContentTypes) {
            if (type.contains(allowedContentType)) {
                return allowedContentType;
            }
        }
        log.error("올바르지 않은 파일 형식입니다. 파일 이름: {}, 파일 타입: {}", file.getOriginalFilename(), file.getContentType());
        throw new InvalidFileFormatException("파일 업로드 실패: 올바르지 않은 파일 형식입니다.");
    }

}
