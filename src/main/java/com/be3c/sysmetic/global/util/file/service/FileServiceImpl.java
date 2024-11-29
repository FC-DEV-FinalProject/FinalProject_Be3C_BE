package com.be3c.sysmetic.global.util.file.service;

import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.dto.FileWithInfoResponse;
import com.be3c.sysmetic.global.util.file.entity.File;
import com.be3c.sysmetic.global.util.file.exception.*;
import com.be3c.sysmetic.global.util.file.repository.FileRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    /* FileRepository와 S3Service는 @Transactional 불가, 수동으로 일관성을 관리해줘야 함 */
    final FileRepository fileRepository;
    final S3Service s3Service;

    @Value("${file.max-size}")
    private long maxFileSize;

    /*
    ----------------------------------------------------------------------------------------
    upload 업로드
     */

    @Override
    public void uploadImage(MultipartFile file, @Valid FileRequest fileRequest) {

        checkFileSize(file);
        checkFileExtension(file, List.of("jpeg", "png", "gif"));

        String path = s3Service.upload(file, fileRequest);
        fileEntityBuilderAndSaver(file, path, fileRequest);

    }

    @Override
    public void uploadPdf(MultipartFile file, @Valid FileRequest fileRequest) {

        checkFileSize(file);
        checkFileExtension(file, List.of("pdf"));

        String path = s3Service.upload(file, fileRequest);
        fileEntityBuilderAndSaver(file, path, fileRequest);
    }

    @Transactional
    @Override
    public void uploadAnyFile(MultipartFile file, @Valid FileRequest fileRequest) {

        checkFileSize(file);

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isEmpty()) {
            log.error("파일 업로드 실패. 파일 이름이 없습니다. 파일 참조 정보: {}", fileRequest);
            throw new InvalidFileFormatException("파일 이름이 없습니다.");
        }

        String extension = originalName.substring(originalName.lastIndexOf('.'));
        if (extension.isEmpty() || extension.isBlank()) {
            log.error("파일 업로드 실패. 올바른 확장자가 포함된 파일이 아닙니다. 파일 참조 정보: {}", fileRequest);
            throw new InvalidFileFormatException("올바른 확장자가 포함된 파일이 아닙니다.");
        }

        String path = s3Service.upload(file, fileRequest);
        fileEntityBuilderAndSaver(file, path, fileRequest);
    }



    /*

    upload 업로드
    ------------------------------------------------------------------------------------------------
    download 다운로드

     */


    @Override
    public String getFilePath(@Valid FileRequest fileRequest) {

        List<File> files = fileRepository.findFilesByFileReference(fileRequest);
        if (files.isEmpty()) {
            log.error("파일을 찾을 수 없습니다. 파일 참조 정보: {}", fileRequest);
            throw new FileNotFoundException();
        }

        File file = files.get(0);
        if(files.size()>1)
            log.warn("파일이 한 개가 아닙니다. 파일 참조 정보: {}", fileRequest);

        return s3Service.createPresignedGetUrl(file.getPath());
    }

    @Override
    public List<String> getFilePaths(@Valid FileRequest fileRequest) {

        List<File> files = fileRepository.findFilesByFileReference(fileRequest);
        checkFilesNotEmpty(files, fileRequest);

        List<String> paths = new ArrayList<>(files.size());
        for (File f : files)
            paths.add(s3Service.createPresignedGetUrl(f.getPath()));

        return paths;
    }

    @Override
    public List<FileWithInfoResponse> getFileWithInfos(@Valid FileRequest fileRequest) {

        List<File> files = fileRepository.findFilesByFileReference(fileRequest);
        checkFilesNotEmpty(files, fileRequest);

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
    public void updateImage(MultipartFile file,@Valid FileRequest fileRequest) {

        checkFileSize(file);
        checkFileExtension(file, List.of("jpeg", "png", "gif"));

        String path = s3Service.upload(file, fileRequest);
        fileEntityBuilderAndSaver(file, path, fileRequest);
    }

    @Transactional
    @Override
    public void updatePdf(MultipartFile file,@Valid FileRequest fileRequest) {

        checkFileSize(file);
        checkFileExtension(file, List.of("pdf"));

        String path = s3Service.upload(file, fileRequest);
        fileEntityBuilderAndSaver(file, path, fileRequest);
    }

    @Override
    public void updateAnyFile(MultipartFile file,@Valid FileRequest fileRequest) {

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
            throw new FileNotFoundException("업데이트 할 파일을 찾을 수 없습니다.");
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
            throw new FileUploadException();
        }
    }


  /*
  update
  ------------------------------------------------------------------------------
  delete
  */

    @Override
    public boolean deleteFile(@Valid FileRequest fileRequest) {

        List<File> files = fileRepository.findFilesByFileReference(fileRequest);
        if (files.isEmpty()) {
            log.error("삭제하려는 파일을 찾을 수 없습니다. 파일 참조 정보: {}", fileRequest);
            throw new FileNotFoundException("삭제할 파일을 찾을 수 없습니다.");
        }

        try {
            fileRepository.delete(files.get(0));
        } catch (DataAccessException e) {
            log.error("파일 삭제 실패 - DB 오류 발생. 파일 참조 정보: {}", fileRequest, e);
            throw new FileDeleteException();
        } catch (Exception e) {
            log.error("파일 삭제 실패. 파일 ID: {}, 파일 경로: {}", files.get(0).getId(), files.get(0).getPath(), e);
            throw new FileDeleteException();
        }

        try{
            return s3Service.deleteObject(files.get(0).getPath());
        } catch (Exception e){
            fileRepository.save(files.get(0));
            throw new FileDeleteException();
        }
    }

    @Override
    public boolean deleteFiles(@Valid FileRequest fileRequest) {

        List<File> files = fileRepository.findFilesByFileReference(fileRequest);

        checkFilesNotEmpty(files, fileRequest);

        try {
            for (File file : files) {
                fileRepository.delete(file);
                s3Service.deleteObject(file.getPath());
            }
        } catch (Exception e) {
            log.error("파일 삭제 실패. 파일 참조 정보: {}", fileRequest, e);
            fileRepository.saveAll(files);
            throw new FileDeleteException();
        }

        return true;
    }

    @Override
    public boolean deleteFileById(Long fileId) {
        Optional<File> file = fileRepository.findById(fileId);
        if (file.isEmpty()) {
            log.error("삭제하려는 파일을 찾을 수 없습니다. 파일 id: {}", fileId);
            throw new FileNotFoundException("삭제할 파일을 찾을 수 없습니다.");
        }

        fileRepository.delete(file.get());

        try {
            s3Service.deleteObject(file.get().getPath());
        } catch (Exception e){
            fileRepository.save(file.get());
            throw new FileDeleteException();
        }
        return true;
    }

/*
  delete
  ------------------------------------------------------------------------------
  custom common private methods
  */


    /**
     * 파일 정보를 DB에 저장하는 메소드
     * S3 업로드 이후에 사용해야 합니다. (롤백 로직 있음)
     * @param file        업로드 할 파일
     * @param s3KeyName   S3에 저장된 path
     * @param fileRequest 파일을 사용할 곳의 정보
     *                    referenceType 참조할 테이블명 referenceId 참조id
     */
    private void fileEntityBuilderAndSaver(MultipartFile file, String s3KeyName, @Valid FileRequest fileRequest) {
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
        } catch (DataAccessException e) {
            s3Service.deleteObject(s3KeyName);
            log.error("파일 정보 DB 저장 실패. 파일 이름: {}, 참조 ID: {}, 참조 타입: {}.",
                    file.getOriginalFilename(), fileRequest.referenceId(), fileRequest.referenceType(), e);
            throw new FileUploadException("파일 정보 저장 중 오류가 발생했습니다.");
        } catch (Exception e) {
            s3Service.deleteObject(s3KeyName);
            log.error("파일 엔티티 저장 실패. 파일 이름: {}, 참조 ID: {}, 참조 타입: {}.",
                    file.getOriginalFilename(), fileRequest.referenceId(), fileRequest.referenceType(), e);
            throw new FileUploadException("파일 업로드 중 오류가 발생했습니다.");
        }
    }

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

        if (file.getSize() > maxFileSize) {
            log.error("파일 크기가 너무 큽니다. 파일 이름: {}, 크기: {}MB, 최대 크기: {}MB",
                    file.getOriginalFilename(), file.getSize() / 1024 / 1024, maxFileSize / 1024 / 1024);
            throw new FileSizeExceededException("파일 크기가 너무 큽니다. 최대 크기는 " + maxFileSize / 1024 / 1024 + "MB입니다.");
        }
    }

    private void checkFilesNotEmpty(List<File> files,@Valid FileRequest fileRequest){
        if (files.isEmpty()) {
            log.error("파일이 비어 있습니다. 파일 참조 정보: {}", fileRequest);
            throw new InvalidFileFormatException("파일이 비어 있습니다.");
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

        String contentType = file.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            log.error("파일의 Content-Type이 비어있거나 null입니다. 파일 이름: {}", file.getOriginalFilename());
            throw new InvalidFileFormatException("Content-Type이 누락되었습니다.");
        }

        for (String allowedContentType : allowedContentTypes) {
            if (contentType.contains(allowedContentType)) {
                return allowedContentType;
            }
        }

        log.error("파일 형식 check fail : 파일 이름: {}, 파일 타입: {}", file.getOriginalFilename(), file.getContentType());
        throw new InvalidFileFormatException("지원되는 파일 형식을 확인해 주세요.");
    }

}
