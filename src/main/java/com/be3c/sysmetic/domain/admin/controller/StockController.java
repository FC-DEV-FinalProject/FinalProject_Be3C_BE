package com.be3c.sysmetic.domain.admin.controller;

import com.be3c.sysmetic.domain.admin.dto.StockRequestDto;
import com.be3c.sysmetic.domain.admin.entity.Stock;
import com.be3c.sysmetic.domain.admin.service.StockService;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.util.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockController {

    private StockService stockService;

    /*
        종목명 중복 검사 메서드
     */
//    @PreAuthorize(("hasRole('ADMIN')"))
    @GetMapping("admin/stock/name")
    public ResponseEntity<ApiResponse<String>> getCheckName(
        @RequestParam String name
    ) throws Exception {
        if(stockService.findItemByName(name).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "중복된 종목명입니다."));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("사용 가능한 종목명입니다."));
    }

    /*
        아이디로 종목 찾기
     */
    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/stock/{stock}")
    public ResponseEntity<ApiResponse<Stock>> getitem(
            @PathVariable Long stock
    ) throws Exception {
        if(stock == null) {
            Optional<Stock> findResult = stockService.findItemById(stock);

            if(findResult.isPresent()) {
                // 아이콘 찾는 코드 추가 필요.
                Stock findStock = findResult.get();
                stockService.findItemIcon(findStock.getId());

                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "해당 종목을 찾을 수 없습니다."));
    }

    /*
        종목 관리 - 종목 페이지 표시.
        RequestParam - page
     */
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/stock")
    public ResponseEntity<ApiResponse<Page<List<Stock>>>> getAllitem(
            @RequestParam Integer page
    ) throws Exception {
        Page<List<Stock>> arrayStock = stockService.findItemPage(page);

        if(arrayStock.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "페이지를 확인해주세요."));
        }
        // 아이콘 찾는 코드 추가 필요.

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(arrayStock));
    }

    /*
        종목 저장하기
     */
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/stock")
    public ResponseEntity<ApiResponse<Integer>> saveitem(
            @RequestBody StockRequestDto stockRequestDto
    ) throws Exception {
        try {
            if(stockRequestDto.getCheckDuplicate()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "잘못된 요청입니다."));
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();

            if(principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                Long userId = ((CustomUserDetails) userDetails).getUserId();

                stockService.saveItem(stockRequestDto, userId);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "잘못된 값입니다."));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success());
    }

    /*
        종목 수정하기
     */
    // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/stock/{stock}")
    public ResponseEntity<ApiResponse<String>> updateItem(@PathVariable("stock") String stock,
                           @RequestBody StockRequestDto stockRequestDto
    ) throws Exception {
        try {
            if(stockRequestDto.getCheckDuplicate()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, "잘못된 요청입니다."));
            }
            if(stockService.updateItem(stockRequestDto)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success());
            }

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
    @DeleteMapping("/admin/stock")
    public void deleteItem(
            @RequestBody StockRequestDto stockRequestDto
    ) throws Exception {
        try {

        } catch (Exception e) {

        }
    }
}
