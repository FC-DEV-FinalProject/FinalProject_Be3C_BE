package com.be3c.sysmetic.domain.admin.controller;

import com.be3c.sysmetic.domain.admin.dto.StockGetResponseDto;
import com.be3c.sysmetic.domain.admin.dto.StockPutRequestDto;
import com.be3c.sysmetic.domain.admin.entity.Stock;
import com.be3c.sysmetic.domain.admin.service.StockService;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.util.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockController {

    private StockService stockService;

    /*
        추가 필요 사항
        1. S3에 아이콘 업로드 / DB 수정 코드
        2. Global Exception Handler - AuthorizationException 처리 핸들러 필요.
     */

    /*
        종목명 중복 검사 메서드
     */
//    @PreAuthorize(("hasRole('ADMIN')"))
    @GetMapping("admin/stock/name")
    public ResponseEntity<ApiResponse<String>> getCheckName(
        @RequestParam String name
    ) throws Exception {
        if(stockService.duplcheck(name)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success("사용 가능한 종목명입니다."));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(ErrorCode.DUPLICATE_RESOURCE, "중복된 종목명입니다."));
    }

    /*
        아이디로 종목 찾기
     */
    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/stock/{stock}")
    public ResponseEntity<ApiResponse<StockGetResponseDto>> getitem(
            @PathVariable Long stock
    ) throws Exception {
        try {
            Stock find_stock = stockService.findItemById(stock);

            StockGetResponseDto stockGetResponseDto = StockGetResponseDto.builder()
                    .id(find_stock.getId())
                    .name(find_stock.getName())
                    // 아이콘 찾는 코드 추가 필요.
                    // .filepath()
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(stockGetResponseDto));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "해당 종목을 찾을 수 없습니다."));
        }
    }

    /*
        종목 관리 - 종목 페이지 표시.
        RequestParam - page
     */
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/stock")
    public ResponseEntity<ApiResponse<Page<Stock>>> getStockPage(
    //나중에 따로 Dto 생성해야함!
    //public ResponseEntity<ApiResponse<Page<StockPageGetRequestDto>>> getStockPage(
            @RequestParam Integer page
    ) throws Exception {
        try {
            Page<Stock> stock_page = stockService.findItemPage(page);

            // 아이콘 찾기 필요.

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(stock_page));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "해당 종목을 찾을 수 없습니다."));
        }
    }

    /*
        종목 저장하기
     */
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/stock")
    public ResponseEntity<ApiResponse<Integer>> saveitem(
            @RequestBody StockPutRequestDto stockRequestDto
    ) throws Exception {
        try {
            if(stockRequestDto.getCheckDuplicate()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "잘못된 요청입니다."));
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();

            if(principal instanceof UserDetails userDetails) {
                Long userId = ((CustomUserDetails) userDetails).getUserId();

                stockService.saveItem(stockRequestDto, userId);
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success());
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "잘못된 값입니다."));
        }
    }

    /*
        종목 수정하기
     */
    // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/stock")
    public ResponseEntity<ApiResponse<String>> updateItem(
            @RequestBody StockPutRequestDto stockPutRequestDto
    ) throws Exception {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();

            if(principal instanceof UserDetails userDetails) {
                Long userId = ((CustomUserDetails) userDetails).getUserId();

                stockService.updateItem(stockPutRequestDto, userId);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR, "수정에 실패했습니다."));
        } catch(NoSuchElementException | IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR, "수정에 실패했습니다."));
        } catch(Exception e) {
            // 예외 처리 코드 추가 필요.
            // 예외 상황 분류 필요.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR, "수정에 실패했습니다."));
        }
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/stock/{id}")
    public ResponseEntity<ApiResponse<String>> deleteItem(
            @PathVariable Long id
    ) throws Exception {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();

            if(principal instanceof UserDetails userDetails) {
                Long userId = ((CustomUserDetails) userDetails).getUserId();

                stockService.deleteItem(id, userId);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "해당 종목을 찾을 수 없습니다."));
        }
    }
}
