package com.be3c.sysmetic.domain.member.controller;

import com.be3c.sysmetic.domain.member.dto.*;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.ErrorCode;
import com.be3c.sysmetic.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@Tag(name = "관심 전략 API", description = "투자자 관심 전략 관련 API")
public interface InterestStrategyControllerDocs {

    /*
        폴더 내 관심 전략을 페이지로 가져오는 api
        1. 해당 관심 전략 페이지를 찾는 데 성공했을 때 : OK
        2. 해당 페이지에 관심 전략이 존재하지 않을 때 : NOT_FOUND
        3. SecurityContext에 userId가 존재하지 않을 때 : FORBIDDEN
     */
    @Operation(summary = "폴더 내 관심 전략 조회", description = "폴더 내 관심 전략을 페이지 단위로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관심 전략 페이지 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InterestStrategyGetResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 페이지에 관심 전략이 존재하지 않음",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "SecurityContext에 userId가 존재하지 않음",
                    content = @Content)
    })
    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    public ResponseEntity<APIResponse<PageResponse<InterestStrategyGetResponseDto>>> getFolderPage(
            InterestStrategyGetRequestDto interestStrategyGetRequestDto
    );

    /*
        관심 전략 등록 api
        1. 관심 전략 등록에 성공했을 때 : OK
        2. 관심 전략 등록에 실패했을 떄 : INTERNAL_SERVER_ERROR
        3. 이미 관심 전략에 등록된 전략일 때 : BAD_REQUEST
        4. 등록할 전략을 찾지 못했을 때 : NOT_FOUND
        5. SecurityContext에 userId가 존재하지 않을 떄 : FORBIDDEN
     */
    @Operation(
            summary = "관심 전략 등록",
            description = "사용자가 관심 전략에 새로운 전략을 등록하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "관심 전략 등록 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "관심 전략 등록 실패 (서버 오류)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 관심 전략에 등록된 전략",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "등록할 전략을 찾지 못함",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            )
    })
    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    public ResponseEntity<APIResponse<String>> follow(
            @Valid @RequestBody FollowPostRequestDto followPostRequestDto
    );

    /*
        관심 전략이 속한 폴더 이동
        1. 폴더 이동 성공했을 때 : OK
        2. 폴더 이동 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 옮기려는 폴더가 존재하지 않을 때 : NOT_FOUND
        4. SecurityContext에 userId가 존재하지 않을 떄 : FORBIDDEN
     */
    @Operation(
            summary = "관심 전략 폴더 이동",
            description = "관심 전략이 속한 폴더를 다른 폴더로 이동하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "폴더 이동 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "폴더 이동 실패 (서버 오류)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "옮기려는 폴더가 존재하지 않음",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<APIResponse<String>> MoveFolder(
            @Valid @RequestBody FollowPutRequestDto followPutRequestDto
    );

    /*
        관심 전략 선택 삭제 api (단일 삭제 포함)
        1. 선택한 관심 전략 전부 삭제에 성공했을 때 : OK
        2. 선택한 관심 전략 중 일부만 삭제에 성공했을 때 : MULTI_STATUS
        3. SecurityContext에 userId가 존재하지 않을 떄 : FORBIDDEN
     */
    @Operation(
            summary = "관심 전략 단일 삭제",
            description = "사용자가 관심 전략을 삭제하는 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "관심 전략 삭제 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            )
    })
    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    public ResponseEntity<APIResponse<String>> unFollow(
            @PathVariable Long id
    );

    @Operation(
            summary = "관심 전략 선택 삭제",
            description = "사용자가 선택한 관심 전략을 삭제하는 API (선택 삭제)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "선택한 관심 전략 전부 삭제 성공",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "207",
                    description = "선택한 관심 전략 중 일부만 삭제 성공 (Multi-Status)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 인증 정보가 없음 (FORBIDDEN)",
                    content = @Content(mediaType = "application/json")
            )
    })
    // @PreAuthorize("hasRole('ROLE_USER') and !hasRole('ROLE_TRADER')")
    public ResponseEntity<APIResponse<Map<Long, String>>> unFollowList(
            @Valid @RequestBody FollowDeleteRequestDto followPostRequestDto
    );
}
