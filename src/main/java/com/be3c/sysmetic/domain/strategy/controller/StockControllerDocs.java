package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.StockGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StockPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StockPutRequestDto;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "종목 API", description = "관리자 종목 API")
public interface StockControllerDocs {

    /*
        종목명 중복 검사 메서드
        1. 동일한 종목 명이 존재하지 않을 떄 : OK
        2. 동일한 종목 명이 존재할 떄 : CONFLICT
        3. SecurityContext에 userId가 존재하지 않을 떄 : FORBIDDEN
     */
    @Operation(summary = "종목명 중복 검사", description = "관리자가 종목명 중복 여부를 확인하는 API")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "중복 검사 성공: 동일한 종목 명이 존재하지 않을 때 'OK' 반환",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "중복 검사 실패: 동일한 종목 명이 존재할 때 'CONFLICT' 반환",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없음: SecurityContext에 userId가 존재하지 않을 때 'FORBIDDEN' 반환",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
//    @PreAuthorize(("hasRole('MANAGER')"))
    public ResponseEntity<APIResponse<String>> getCheckDupl(
            @NotBlank @RequestParam String name
    );

    /*
        단일 종목 찾기
        1. 해당 아이디의 종목을 찾는 데 성공했을 때 : OK
        2. 해당 아이디의 종목을 찾는 데 실패했을 떄 : NOT_FOUND
     */
    @Operation(summary = "단일 종목 찾기", description = "관리자가 특정 아이디를 기준으로 단일 종목을 조회하는 API")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공: 해당 아이디의 종목을 찾는 데 성공했을 때 'OK' 반환",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "조회 실패: 해당 아이디의 종목을 찾는 데 실패했을 때 'NOT_FOUND' 반환",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
//    @PreAuthorize(("hasRole('MANAGER')"))
    public ResponseEntity<APIResponse<StockGetResponseDto>> getItem(
            @NotBlank @PathVariable Long id
    );

    /*
        종목 관리 - 종목 페이지 표시.
        1. 해당 페이지의 종목을 찾는 데 성공했을 때 : OK
        2. 해당 페이지에 아무런 종목이 존재하지 않을 때 : NOT_FOUND
        3. 해당 페이지의 종목을 찾는 데 실패했을 때 : INTERNAL_SERVER_ERROR
     */
    @Operation(summary = "종목 페이지 표시", description = "관리자가 특정 페이지의 종목 리스트를 조회하는 API")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공: 해당 페이지의 종목을 찾는 데 성공했을 때 'OK' 반환",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "조회 실패: 해당 페이지에 아무런 종목이 존재하지 않을 때 'NOT_FOUND' 반환",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러: 해당 페이지의 종목을 찾는 데 실패했을 때 'INTERNAL_SERVER_ERROR' 반환",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
//    @PreAuthorize(("hasRole('MANAGER')"))
    @GetMapping("/admin/stocklist/{page}")
    public ResponseEntity<APIResponse<PageResponse<StockGetResponseDto>>> getStockPage(
            @NotBlank @PathVariable Integer page
    );

    /*
        종목 저장하기
        1. 종목을 저장하는 데 성공했을 때 : OK
        2. 종목을 저장하는 데 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 중복 검사가 되지 않은 요청일 때 : BAD_REQUEST
        4. NOT NULL 값에 NULL이 들어왔을 때 : BAD_REQUEST
        5. 중복된 종목명이 존재할 때 : BAD_REQUEST
     */
    @Operation(summary = "종목 저장하기", description = "관리자가 새로운 종목을 저장하는 API")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "저장 성공: 종목을 저장하는 데 성공했을 때 'OK' 반환",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러: 종목을 저장하는 데 실패했을 때 'INTERNAL_SERVER_ERROR' 반환",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청: 중복 검사가 되지 않은 요청일 때," +
                                    "NOT NULL 값에 NULL이 들어왔을 때, 또는" +
                                    "중복된 종목명이 존재할 때 'BAD_REQUEST' 반환",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
//    @PreAuthorize(("hasRole('MANAGER')"))
    @PostMapping("/admin/stock")
    public ResponseEntity<APIResponse<String>> saveitem(
            @Valid @RequestBody StockPostRequestDto stockPostRequestDto
    );

    /*
        종목 수정하기
        1. 종목을 수정하는 데 성공했을 때 : OK
        2. 종목을 수정하는 데 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 수정해야할 종목을 찾지 못했을 때 : NOT_FOUND
     */
    @Operation(summary = "종목 수정하기", description = "트레이더가 기존 종목의 정보를 수정하는 API")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "수정 성공: 종목을 수정하는 데 성공했을 때 'OK' 반환",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러: 종목을 수정하는 데 실패했을 때 'INTERNAL_SERVER_ERROR' 반환",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "수정 실패: 수정해야 할 종목을 찾지 못했을 때 'NOT_FOUND' 반환",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
//    @PreAuthorize(("hasRole('MANAGER')"))
    @PutMapping("/admin/stock")
    public ResponseEntity<APIResponse<String>> updateItem(
            @Valid @RequestBody StockPutRequestDto stockPutRequestDto
    );

    /*
        종목 삭제하기 api
        1. 종목을 삭제하는 데 성공했을 때 : OK
        2. 종목을 삭제하는 데 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 삭제할 종목을 찾지 못했을 때 : NOT_FOUND
     */
    @Operation(summary = "종목 삭제하기", description = "관리자가 기존 종목을 삭제하는 API")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "삭제 성공: 종목을 삭제하는 데 성공했을 때 'OK' 반환",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러: 종목을 삭제하는 데 실패했을 때 'INTERNAL_SERVER_ERROR' 반환",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "삭제 실패: 삭제할 종목을 찾지 못했을 때 'NOT_FOUND' 반환",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    //    @PreAuthorize(("hasRole('MANAGER')"))
    @DeleteMapping("/admin/stock/{id}")
    public ResponseEntity<APIResponse<String>> deleteItem(
            @NotBlank @PathVariable Long id
    );
}
