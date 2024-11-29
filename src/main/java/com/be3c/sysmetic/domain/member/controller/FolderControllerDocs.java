package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.FolderListResponseDto;
import com.be3c.sysmetic.domain.member.dto.FolderPostRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderPutRequestDto;
import com.be3c.sysmetic.global.common.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "관심 전략 폴더 API", description = "투자자 관심 전략 폴더 관련 API")
public interface FolderControllerDocs {

    @Operation(
            summary = "폴더명 중복 확인",
            description = "폴더명이 중복되었는지 확인하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "중복된 폴더명이 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복된 폴더명이 존재함",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            )
    })
    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    ResponseEntity<APIResponse<String>> getDuplCheck(
            @RequestParam String folderName
    );

    /*
        해당 유저의 폴더 목록을 반환하는 api
        1. 해당 유저의 폴더를 찾았다면 : OK
        2. 해당 유저의 폴더 개수가 0개라면 : NOT_FOUND
        3. SecurityContext에 userId가 존재하지 않을 떄 : FORBIDDEN
     */
    @Operation(
            summary = "유저 폴더 목록 조회",
            description = "해당 유저의 폴더 목록을 반환하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "해당 유저의 폴더 목록 반환 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 유저의 폴더가 존재하지 않음",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            )
    })
    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    ResponseEntity<APIResponse<List<FolderListResponseDto>>> getAllFolder(
    );

    /*
        폴더 추가 메서드
        1. 폴더 추가에 성공했을 떄 : OK
        2. 폴더 추가에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 중복체크가 되지 않은 요청일 떄 : BAD_REQUEST
        4. 중복된 이름의 폴더가 존재할 때 : CONFLICT
        5. 현재 폴더 개수가 5개일 때 : TOO_MANY_REQUESTS
        6. SecurityContext에 userId가 존재하지 않을 떄 : FORBIDDEN
     */
    @Operation(
            summary = "폴더 추가",
            description = "새로운 폴더를 추가하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "폴더 추가 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "폴더 추가 실패 (서버 오류)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "중복 체크가 되지 않은 요청 (잘못된 요청)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복된 폴더 이름이 존재",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "현재 폴더 개수가 허용 한도를 초과",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<APIResponse<String>> postFolder(
            @Valid @RequestBody FolderPostRequestDto folderPostRequestDto
    );

    /*
        폴더명 수정 메서드
        1. 폴더 수정에 성공했을 때 : OK
        2. 폴더 수정에 실패했을 떄 : INTERNAL_SERVER_ERROR
        3. 중복 체크가 진행되지 않은 요청일 때 : BAD_REQUEST
        4. 수정하려는 폴더를 찾지 못했을 때 : NOT_FOUND
        5. 중복된 이름의 폴더가 존재할 때 : CONFLICT
        6. SecurityContext에 userId가 존재하지 않을 떄 : FORBIDDEN
     */
    @Operation(
            summary = "폴더명 수정",
            description = "기존 폴더명을 새로운 이름으로 수정하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "폴더 수정 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "폴더 수정 실패 (서버 오류)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "중복 체크가 진행되지 않은 요청 (잘못된 요청)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수정하려는 폴더를 찾지 못함",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복된 폴더 이름이 존재",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            )
    })
    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    ResponseEntity<APIResponse<String>> putFolder(
            @Valid @RequestBody FolderPutRequestDto folderPutRequestDto
    );

    /*
        폴더 삭제 api
        1. 폴더 삭제에 성공횄을 때 : OK
        2. 폴더 삭제에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 현재 폴더 개수가 1개 이하일 때 : UNPROCESSABLE_ENTITY
        4. 삭제하려는 폴더를 찾지 못했을 때 : NOT_FOUND
        5. SecurityContext에 userId가 존재하지 않을 떄 : FORBIDDEN
     */
    @Operation(
            summary = "폴더 삭제",
            description = "폴더를 삭제하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "폴더 삭제 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "폴더 삭제 실패 (서버 오류)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "현재 폴더 개수가 1개 이하로 삭제 불가",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제하려는 폴더를 찾지 못함",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<APIResponse<String>> deleteFolder(
            @PathVariable Long id
    );
}
