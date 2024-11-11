package com.be3c.sysmetic.global.util.file.service;

import com.be3c.sysmetic.global.util.file.dto.FileDto;
import com.be3c.sysmetic.global.util.file.dto.FileResponseDto;
import com.be3c.sysmetic.global.util.file.dto.FileRequestDto;
import com.be3c.sysmetic.global.util.file.entity.File;
import com.be3c.sysmetic.global.util.file.exception.InvalidFileFormatException;
import com.be3c.sysmetic.global.util.file.mapper.FileMapper;
import com.be3c.sysmetic.global.util.file.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class FileServiceImpl implements FileService{

    final FileRepository fileRepository;
    final FileMapper fileMapper;
    final S3Service s3Service;

    public FileServiceImpl(FileRepository fileRepository, FileMapper fileMapper, S3Service s3Service) {
        this.fileRepository = fileRepository;
        this.fileMapper = fileMapper;
        this.s3Service = s3Service;
    }

    /**
     * 파일 정보를 DB에 저장하는 메소드
     * @param file 업로드 할 파일
     * @param s3KeyName S3에 저장된 path
     * @param fileRequestDto 파일을 사용할 곳의 정보
     *                         referenceType 참조할 테이블명 referenceId 참조id
     */
    private void fileEntityBuilderAndSaver(MultipartFile file, String s3KeyName, FileRequestDto fileRequestDto) {
        File fileEntity = File.builder()
                .path(s3KeyName)
                .type(file.getContentType())
                .size(file.getSize())
                .originalName(file.getOriginalFilename())
                .referenceType(fileRequestDto.referenceType())
                .referenceId(fileRequestDto.referenceId())
                .build();
        fileRepository.save(fileEntity);
    }

    /*
    ----------------------------------------------------------------------------------------
    upload 업로드
     */

    @Transactional
    @Override
    public String uploadImage(MultipartFile file, FileRequestDto fileRequestDto) {
        String type = file.getContentType();
        String extension;
        if(type.contains("jpeg")){  // null 조심
            extension = ".jpg";
        }
        else if(type.contains("png")){
            extension = ".png";
        }
        else if(type.contains("gif")){
            extension = ".gif";
        }
        else{
            throw new InvalidFileFormatException("파일 업로드 실패: 이미지 형식이 올바르지 않습니다.");
        }

        String path = s3Service.upload(file, fileRequestDto, extension);
        fileEntityBuilderAndSaver(file, path, fileRequestDto);

        return path;
    }

    @Transactional
    @Override
    public String uploadPdf(MultipartFile file, FileRequestDto fileRequestDto) {
        String type = file.getContentType();
        String extension;
        if(type.contains("pdf")){
            extension = ".pdf";
        } else {
            throw new InvalidFileFormatException("파일 업로드 실패: pdf 파일 형식이 올바르지 않습니다.");
        }

        String path = s3Service.upload(file, fileRequestDto, extension);
        fileEntityBuilderAndSaver(file, path, fileRequestDto);

        return path;
    }

    @Transactional
    @Override
    public String uploadAnyFile(MultipartFile file, FileRequestDto fileRequestDto) {

        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf('.'));   // extention 없을 경우에 대한 에러 처리

        fileEntityBuilderAndSaver(file, extension, fileRequestDto);

        String path = s3Service.upload(file, fileRequestDto, extension);
        fileEntityBuilderAndSaver(file, path, fileRequestDto);

        return path;
    }


    /*

    upload 업로드
    ------------------------------------------------------------------------------------------------
    download 다운로드

     */


    @Override
    public String getFilePath(FileRequestDto fileRequestDto) {
        List<File> files = fileRepository.findFilesByFileReference(fileRequestDto);
        File file = files.get(0);
        // size 2 이상일 시 로깅하기




        return s3Service.createPresignedGetUrl(file.getPath());
    }




    @Override
    public Map<Long, FileResponseDto> getFileResponseByReferenceId(FileRequestDto fileRequestDto) {
        List<File> files = fileRepository.findFilesByFileReference(fileRequestDto);


        Map<Long, FileResponseDto> map = new HashMap<>();
        for(File f : files){
            String url = s3Service.createPresignedGetUrl(f.getPath());
            FileResponseDto fileResponseDto = new FileResponseDto(f.getReferenceId(), url, f.getOriginalName());
            map.put(f.getReferenceId(), fileResponseDto);
        }




        return map;
    }


//    @Transactional
//    @Override
//    public List<FileResponseDto> getFileResponse(FileRequestDto fileRequestDto) {
//        List<File> files = fileRepository.findFilesByFileReference(fileRequestDto);
//        List<FileResponseDto> lists = new ArrayList<>();
//
//        for(File f : files){
//            String url = s3Service.createPresignedGetUrl(f.getPath());
//            FileResponseDto fileResponseDto = new FileResponseDto(f.getReferenceId(), url, f.getOriginalName());
//            lists.add(fileResponseDto);
//        }
//
//        return lists;
//    }


  /*
  download
  ------------------------------------------------------------------------------
  file info
   */

}
