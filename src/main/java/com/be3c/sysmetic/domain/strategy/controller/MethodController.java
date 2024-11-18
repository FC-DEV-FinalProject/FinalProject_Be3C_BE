package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.MethodGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodPutRequestDto;
import com.be3c.sysmetic.domain.strategy.service.MethodService;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.exception.ConflictException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MethodController {

    private final MethodService methodService;

    /*
        매매 방식 조회 (개별)
        매매 방식 조회 (페이지)
        매매 방식 아이콘 조회
        매매 방식 등록
        매매 방식 수정
        매매 방식 삭제
     */

    /*
        중복 확인 메서드
        1. url에 문자 or 숫자로 이루어진 값이 넘어온다면 이 메서드로 넘어온다.
        2. url의 값을 name에 저장한다.
        3. 해당 값으로 SELECT WHERE name = :name을 진행한다.
        4. SELECT 값이 존재한다면? DEPLICATE_RESOURCE 코드를 반환한다.
        5. SELECT 값이 존재하지 않는다면? OK 코드를 반환한다.
     */
    @GetMapping("/admin/method/availability")
    public ResponseEntity<APIResponse<String>> duplCheck(
            @RequestParam String name
    ) throws Exception {
        if(methodService.duplCheck(name)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.success());
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(APIResponse.fail(ErrorCode.DUPLICATE_RESOURCE, "중복된 이름입니다."));
    }

    /*
        1. 만약 숫자로만 이루어진 값이 PathVariable로 넘어온다면, 해당 메서드로 진입한다.
        2.
     */
//    @GetMapping("/admin/method/{id:[0-9]+}")
    @GetMapping("/admin/method/{id}")
    public ResponseEntity<APIResponse<MethodGetResponseDto>> getMethod(
            @PathVariable Long id
    ) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.success(methodService.findById(id)));
        } catch (IllegalArgumentException |
                 DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }

    @GetMapping("/admin/methodlist")
    public ResponseEntity<APIResponse<PageResponse<MethodGetResponseDto>>> getMethods(
            @RequestParam Integer page
    ) throws Exception {
        try {
            PageResponse<MethodGetResponseDto> method_page = methodService.findMethodPage(page);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.success(method_page));
        } catch (EntityNotFoundException |
                 NoSuchElementException |
                 IllegalArgumentException |
                 DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST));
        }
    }

    @PostMapping("/admin/method")
    public ResponseEntity<APIResponse<String>> postMethod(
            @Valid @RequestBody MethodPostRequestDto method_post_request
    ) throws Exception {
        try {
            if(methodService.insertMethod(method_post_request)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        } catch (IllegalArgumentException |
                 IllegalStateException |
                 DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST));
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(APIResponse.fail(ErrorCode.DUPLICATE_RESOURCE));
        }
    }

    /*
        매매 방식 수정 메서드
        1. HttpMethod가 put이라면, 이 메서드로 온다.
        2. MethodService.updateMethod를 호출한다.
     */
    @PutMapping("/admin/method")
    public ResponseEntity<APIResponse<String>> putMethod(
            @Valid @RequestBody MethodPutRequestDto method_put_request
    ) throws Exception {
        try {
            if(methodService.updateMethod(method_put_request)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST));
        } catch (IllegalArgumentException |
                 IllegalStateException |
                 DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(APIResponse.fail(ErrorCode.DUPLICATE_RESOURCE));
        }
    }

    /*
        매매 유형 삭제 메서드
     */
//    @DeleteMapping("/admin/method/{id:[0-9]+}")
    @DeleteMapping("/admin/method/{id}")
    public ResponseEntity<APIResponse<String>> deleteMethod(
            @PathVariable Long id
    ) throws Exception {
        try {
            if(methodService.deleteMethod(id)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(APIResponse.success());
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(APIResponse.success());
        } catch (IllegalArgumentException |
                 DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.fail(ErrorCode.BAD_REQUEST));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.fail(ErrorCode.NOT_FOUND));
        }
    }
}
