package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.StockGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StockPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StockPutRequestDto;
import com.be3c.sysmetic.domain.strategy.service.StockService;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Controller
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockController {

    private final StockService stockService;

    /*
        추가 필요 사항
        1. S3에 아이콘 업로드 / DB 수정 코드
        2. Global Exception Handler - AuthorizationException 처리 핸들러 필요.
     */

    /*
        종목명 중복 검사 메서드
     */
//    @PreAuthorize(("hasRole('MANAGER')"))
    @GetMapping("admin/stock/availability")
    public ResponseEntity<ApiResponse<String>> getCheckName(
        @RequestParam String name
    ) throws Exception {
        try {
                if(stockService.duplcheck(name)) {
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(ApiResponse.success("사용 가능한 종목명입니다."));
                }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.fail(ErrorCode.DUPLICATE_RESOURCE, "중복된 종목명입니다."));
        } catch (AuthenticationCredentialsNotFoundException
                 | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN, "중복된 종목명입니다."));
        }
    }

    /*
        아이디로 종목 찾기
     */
//    @PreAuthorize(("hasRole('MANAGER')"))
    @GetMapping("/admin/stock/{id}")
    public ResponseEntity<ApiResponse<StockGetResponseDto>> getitem(
            @PathVariable Long id
    ) throws Exception {
        try {
            StockGetResponseDto find_stock = stockService.findItemById(id);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(find_stock));
        } catch (NoSuchElementException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "해당 종목을 찾을 수 없습니다."));
        } catch (AuthenticationCredentialsNotFoundException
                 | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN, "중복된 종목명입니다."));
        }
    }

    /*
        종목 관리 - 종목 페이지 표시.
        RequestParam - page
     */
//    @PreAuthorize(("hasRole('MANAGER')"))
    @GetMapping("/admin/stocklist/{page}")
    public ResponseEntity<ApiResponse<PageResponse<StockGetResponseDto>>> getStockPage(
            @PathVariable Integer page
    ) throws Exception {
        try {
            PageResponse<StockGetResponseDto> stock_page = stockService.findItemPage(page);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(stock_page));
        } catch (NoSuchElementException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "해당 종목을 찾을 수 없습니다."));
        } catch (AuthenticationCredentialsNotFoundException
                 | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN, "중복된 종목명입니다."));
        }
    }

    /*
        종목 저장하기
     */
//    @PreAuthorize(("hasRole('MANAGER')"))
    @PostMapping("/admin/stock")
    public ResponseEntity<ApiResponse<String>> saveitem(
            @RequestBody StockPostRequestDto stockRequestDto
    ) throws Exception {
        try {
            if(stockService.saveItem(stockRequestDto)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST));
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, e.getMessage()));
        } catch (AuthenticationCredentialsNotFoundException |
                 UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN));
        }
    }

    /*
        종목 수정하기
     */
//    @PreAuthorize(("hasRole('MANAGER')"))
    @PutMapping("/admin/stock")
    public ResponseEntity<ApiResponse<String>> updateItem(
            @RequestBody StockPutRequestDto stockPutRequestDto
    ) throws Exception {
        try {
            if(stockService.updateItem(stockPutRequestDto)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST));
        } catch(NoSuchElementException | IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR, "수정에 실패했습니다."));
        } catch (AuthenticationCredentialsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN));
        }
    }

    //    @PreAuthorize(("hasRole('MANAGER')"))
    @DeleteMapping("/admin/stock/{id}")
    public ResponseEntity<ApiResponse<String>> deleteItem(
            @PathVariable Long id
    ) throws Exception {
        try {
            if(stockService.deleteItem(id)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST));
        } catch (NoSuchElementException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, e.getMessage()));
        } catch (AuthenticationCredentialsNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail(ErrorCode.FORBIDDEN));
        }
    }
}
