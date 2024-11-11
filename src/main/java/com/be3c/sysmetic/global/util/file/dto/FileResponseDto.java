package com.be3c.sysmetic.global.util.file.dto;

/**
 * 파일에 접근할 수 있는 presigned url을 응답
 * @param referenceId 파일이 연관된 정보의 id
 * @param url 파일에 접근할 수 있는 url
 * @param originalName 파일 원본명
 */
public record FileResponseDto(
        Long referenceId,
        String url,
        String originalName
) {
}
