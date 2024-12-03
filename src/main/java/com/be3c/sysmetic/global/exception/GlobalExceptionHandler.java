package com.be3c.sysmetic.global.exception;

import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.util.file.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.be3c.sysmetic.global.util.email.exception.EmailSendingException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<APIResponse<?>> handleEmailSendingException(EmailSendingException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR, "이메일 전송 오류 발생"));
    }

    @ExceptionHandler(FileDeleteException.class)
    public ResponseEntity<APIResponse<?>> handleFileDeleteException(FileDeleteException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR, "파일 삭제 중 오류가 발생했습니다. " + e.getMessage()));
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<APIResponse<?>> handleFileNotFoundException(FileNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(APIResponse.fail(ErrorCode.NOT_FOUND, "파일을 찾을 수 없습니다: " + e.getMessage()));
    }

    @ExceptionHandler(FileSizeExceededException.class)
    public ResponseEntity<APIResponse<?>> handleFileSizeExceededException(FileSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.fail(ErrorCode.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<APIResponse<?>> handleFileUploadException(FileUploadException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다. " + e.getMessage()));
    }

    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<APIResponse<?>> handleInvalidFileFormatException(InvalidFileFormatException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.fail(ErrorCode.BAD_REQUEST, "파일의 형식이 올바르지 않습니다." + e.getMessage()));
    }

}
