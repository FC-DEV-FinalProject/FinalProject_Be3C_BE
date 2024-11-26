package com.be3c.sysmetic.domain.strategy.controller;

import com.be3c.sysmetic.domain.strategy.dto.PageReplyResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyDeleteRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyGetPageRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.ReplyPostRequestDto;
import com.be3c.sysmetic.global.common.response.APIResponse;
import com.be3c.sysmetic.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "전략 댓글 API", description = "전략 댓글 기능")
public interface StrategyReplyControllerDocs {

    /*
        전략의 댓글 페이지 요청 API
        1. 댓글 목록을 불러오는 데 성공했을 때 : OK
        2. 해당 전략을 찾지 못했을 때 : NOT_FOUND
        3. 해당 페이지에 아무런 댓글이 존재하지 않을 때 : BAD_REQUEST
     */
    @Operation(summary = "댓글 페이지 요청", description = "특정 전략의 댓글 목록을 페이지별로 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageReplyResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 전략을 찾을 수 없음",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "요청한 페이지에 댓글 없음",
                    content = @Content)
    })
    public ResponseEntity<APIResponse<PageResponse<PageReplyResponseDto>>> getReplyPage(
            ReplyGetPageRequestDto replyGetPageRequestDto
    );

    /*
        댓글 등록 API
        1. 댓글 등록에 성공했을 때 : OK
        2. 댓글 등록에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 댓글을 달 전략을 찾지 못했을 때 : NOT_FOUND
     */
    @Operation(summary = "댓글 등록", description = "특정 전략에 댓글을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReplyPostRequestDto.class))),
            @ApiResponse(responseCode = "500", description = "댓글 등록 실패 (서버 에러)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "댓글을 달 전략을 찾을 수 없음",
                    content = @Content)
    })
    public ResponseEntity<APIResponse<String>> postReply(
            @RequestBody ReplyPostRequestDto replyPostRequestDto
    );

    /*
        댓글 삭제 API
        1. 댓글 등록에 성공했을 때 : OK
        2. 댓글 등록에 실패했을 때 : INTERNAL_SERVER_ERROR
        3. 댓글을 찾지 못했을 때 : NOT_FOUND
     */
    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "댓글 삭제 실패 (서버 에러)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음",
                    content = @Content)
    })
    // @PreAuthorize("hasRole='ROLE_USER and !ROLE_TRADER'")
    public ResponseEntity<APIResponse<String>> deleteReply(
            @RequestBody ReplyDeleteRequestDto replyDeleteRequestDto
    );
}
