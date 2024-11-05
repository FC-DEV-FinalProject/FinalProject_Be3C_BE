package com.be3c.sysmetic.global.common.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    // 성공
    public static <T> ApiResponse<T> success(){
        return new ApiResponse<>(SuccessCode.OK.getStatusCode(), SuccessCode.OK.getMessage(), null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SuccessCode.OK.getStatusCode(), SuccessCode.OK.getMessage(), data);
    }

    public static <T> ApiResponse<T> success(SuccessCode code, T data) {
        return new ApiResponse<>(code.getStatusCode(), code.getMessage(), data);
    }

    public static <T> ApiResponse<T> success(SuccessCode code, String message, T data) {
        return new ApiResponse<>(code.getStatusCode(), message, data);
    }

    public static <T> ApiResponse<T> fail(ErrorCode code){
        return new ApiResponse<>(code.getStatusCode(), code.getMessage(), null);
    }

    public static <T> ApiResponse<T> fail(ErrorCode code, String message) {
        return new ApiResponse<>(code.getStatusCode(), message, null);
    }

    public static <T> ApiResponse<T> create(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }
}

