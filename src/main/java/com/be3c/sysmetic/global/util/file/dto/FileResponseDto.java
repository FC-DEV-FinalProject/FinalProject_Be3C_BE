package com.be3c.sysmetic.global.util.file.dto;

/**
 * 파일에 접근할 수 있는 url과 원본 파일명을 응답
 * @param url 파일에 접근할 수 있는 url
 * @param originalName 파일 원본명
 */
public record FileResponseDto(
        String url,
        String originalName
) {
}
