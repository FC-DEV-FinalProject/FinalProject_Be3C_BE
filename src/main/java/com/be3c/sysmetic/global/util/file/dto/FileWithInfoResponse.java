package com.be3c.sysmetic.global.util.file.dto;

/**
 * 파일에 접근할 수 있는 url과 메타 데이터 응답
 * @param id 파일id
 * @param url 파일에 접근할 수 있는 url
 * @param originalName 파일 원본명
 * @param fileSize 파일 사이즈
 */
public record FileWithInfoResponse(
        Long id,
        String url,
        String originalName,
        Long fileSize
) {
}
