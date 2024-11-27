package com.be3c.sysmetic.global.util.file.dto;

/**
 * 해당 파일이 필요한 곳의 정보로 파일을 요청
 * @param referenceType 참조할 테이블명 e.g FileReferenceType.STRATAGY
 * @param referenceId 참조할 컬럼 ID
 */
public record FileRequest(
        FileReferenceType referenceType,
        Long referenceId
) {
}
