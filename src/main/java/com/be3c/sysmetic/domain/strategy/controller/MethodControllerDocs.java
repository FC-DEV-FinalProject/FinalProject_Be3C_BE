package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.MethodGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodPutRequestDto;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "매매 방식 API", description = "관리자 매매 방식 API")
public interface MethodControllerDocs {

    /*
        매매 유형 명 중복 확인 Api
        1. 중복된 이름의 매매 유형이 존재하지 않을 때 : OK
        2. 중복된 이름의 매매 유형이 존재할 때 : CONFLICT
     */
    @Operation(
            summary = "매매 유형 명 중복 확인",
            description = "매매 유형 명이 중복되었는지 확인하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "중복된 매매 유형 명이 없음",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복된 매매 유형 명이 존재함",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<APIResponse<String>> getCheckDupl(
            @NotBlank @RequestParam String name
    );

    /*
        매매 유형 찾기 Api
        1. 매매 유형 찾기 성공했을 때 : OK
        2. 매매 유형 찾기 실패했을 때 : NOT_FOUND
     */
    @Operation(
            summary = "매매 유형 찾기",
            description = "매매 유형 Id로 매매 유형 정보를 찾는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "매매 유형 찾기 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MethodGetResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Id를 포함하지 않은 요청",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "매매 유형 찾기 실패",
                    content = @Content(mediaType = "application/json")
            )
    })
//    @GetMapping("/admin/method/{id:[0-9]+}")
    public ResponseEntity<APIResponse<MethodGetResponseDto>> getMethod(
            @NotBlank @PathVariable Long id
    );

    /*
        매매 유형 페이지로 찾기 api
        1. 매매 유형 페이지 찾기 성공했을 떄 : OK
        2. 페이지에 아무런 데이터도 존재하지 않을 때 : NOT_FOUND
        3. 잘못된 데이터가 입력됐을 때 : BAD_REQUEST
     */
    @Operation(
            summary = "매매 유형 페이지로 찾기",
            description = "매매 유형을 페이지네이션 방식으로 조회하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "매매 유형 페이지 찾기 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "페이지에 아무런 데이터도 존재하지 않음 / 페이지를 입력하지 않은 요청",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 데이터 입력",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<APIResponse<PageResponse<MethodGetResponseDto>>> getMethods(
            @NotBlank @RequestParam Integer page
    );

    /*
        매매 유형 등록 api
        1. 매매 유형 등록에 성공했을 때 : OK
        2. 매매 유형 등록에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 중복 체크를 진행하지 않은 요청일 때 : BAD_REQUEST
        4. 중복된 매매 유형명이 존재할 때 : CONFLICT
     */
    @Operation(
            summary = "매매 유형 등록",
            description = "새로운 매매 유형을 등록하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "매매 유형 등록 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "중복 체크를 진행하지 않은 요청",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복된 매매 유형명이 존재",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "매매 유형 등록 실패 (서버 에러)",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<APIResponse<String>> postMethod(
            @Valid @RequestBody MethodPostRequestDto methodPostRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file
    );

    /*
        매매 유형 수정 메서드
        1. 매매 유형 수정에 성공했을 때 : OK
        2. 매매 유형 수정에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 중복 체크를 진행하지 않은 요청일 때 : BAD_REQUEST
        4. 수정하려는 매매 유형이 존재하지 않을 때 : NOT_FOUND
        5. 동일한 매매 유형명이 존재할 때 : CONFLICT
     */
    @Operation(
            summary = "매매 유형 수정",
            description = "기존 매매 유형 정보를 수정하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "매매 유형 수정 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "중복 체크를 진행하지 않은 요청",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수정하려는 매매 유형이 존재하지 않음",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "동일한 매매 유형명이 존재",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "매매 유형 수정 실패 (서버 에러)",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<APIResponse<String>> putMethod(
            @Valid @RequestBody MethodPutRequestDto methodPutRequestDto
    );

    /*
        매매 유형 삭제 메서드
        1. 매매 유형 삭제에 성공했을 때 : OK
        2. 매매 유형 삭제에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 삭제하려는 매매 유형을 찾지 못했을 때 : NOT_FOUND
     */
//    @DeleteMapping("/admin/method/{id:[0-9]+}")
    @Operation(
            summary = "매매 유형 삭제",
            description = "기존 매매 유형을 삭제하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "매매 유형 삭제 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제하려는 매매 유형을 찾지 못함",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "매매 유형 삭제 실패 (서버 에러)",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<APIResponse<String>> deleteMethod(
            @NotBlank @PathVariable Long id
    );
}
