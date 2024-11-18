package com.be3c.sysmetic.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
    /*
        200 OK
     */
    OK(HttpStatus.OK, "요청이 성공했습니다."),
    UPDATED(HttpStatus.OK, "리소스가 성공적으로 업데이트되었습니다."),
    DELETED(HttpStatus.OK, "리소스가 성공적으로 삭제되었습니다."),

    /*
       201 Created
     */
    CREATED(HttpStatus.CREATED, "리소스가 성공적으로 생성되었습니다."),

    /*
       204 No Content
     */
    NO_CONTENT(HttpStatus.NO_CONTENT, "성공적으로 처리되었으나 반환할 내용이 없습니다.");

    private final HttpStatus status;
    private final String message;

    public int getStatusCode() {
        return status.value();
    }
}