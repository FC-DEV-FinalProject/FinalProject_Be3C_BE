package com.be3c.sysmetic.global.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@Schema(description="API 응답 공통 포맷")
public class APIResponse<T> {
    
    @Schema(description="응답 코드")
    private int code;
    
    @Schema(description="응답 메시지")
    private String message;
    
    @Schema(description="데이터")
    private T data;

    // 성공
    public static <T> APIResponse<T> success(){
        return new APIResponse<>(SuccessCode.OK.getStatusCode(), SuccessCode.OK.getMessage(), null);
    }

    public static <T> APIResponse<T> success(T data) {
        return new APIResponse<>(SuccessCode.OK.getStatusCode(), SuccessCode.OK.getMessage(), data);
    }

    public static <T> APIResponse<T> success(SuccessCode code, T data) {
        return new APIResponse<>(code.getStatusCode(), code.getMessage(), data);
    }

    public static <T> APIResponse<T> success(SuccessCode code, String message, T data) {
        return new APIResponse<>(code.getStatusCode(), message, data);
    }

    public static <T> APIResponse<T> fail(ErrorCode code){
        return new APIResponse<>(code.getStatusCode(), code.getMessage(), null);
    }

    public static <T> APIResponse<T> fail(ErrorCode code, String message) {
        return new APIResponse<>(code.getStatusCode(), message, null);
    }

    public static <T> APIResponse<T> create(int code, String message, T data) {
        return new APIResponse<>(code, message, data);
    }

    public static <T> APIResponse<T> create() {
        return new APIResponse<>(SuccessCode.CREATED.getStatusCode(), SuccessCode.CREATED.getMessage(), null);
    }
}