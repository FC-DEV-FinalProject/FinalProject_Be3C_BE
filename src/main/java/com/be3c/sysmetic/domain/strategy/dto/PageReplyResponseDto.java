package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@Schema(description = "댓글 응답 정보를 담는 DTO")
public class PageReplyResponseDto {

    @Schema(description = "댓글 ID", example = "1")
    @JsonProperty("replyId")
    private Long replyId;

    @Schema(description = "전략 ID", example = "12345")
    @JsonProperty("strategyId")
    private Long strategyId;

    @Schema(description = "회원 ID", example = "67890")
    @JsonProperty("memberId")
    private Long memberId;

    @Schema(description = "회원 닉네임", example = "테스트닉네임")
    @JsonProperty("memberNickname")
    private String memberNickname;

    @Schema(description = "댓글 내용", example = "이 전략 정말 유용하네요!")
    @JsonProperty("content")
    private String content;

    @Schema(description = "댓글 등록일")
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime replyCreatedAt;

    @Schema(description = "사용자 프로필 이미지")
    @JsonProperty("memberProfilePath")
    private String memberProfilePath;

    public PageReplyResponseDto(Long replyId, Long strategyId, Long memberId, String memberNickname, String content, LocalDateTime replyCreatedAt, String memberProfilePath) {
        this.replyId = replyId;
        this.strategyId = strategyId;
        this.memberId = memberId;
        this.memberNickname = memberNickname;
        this.content = content;
        this.replyCreatedAt = replyCreatedAt;
        this.memberProfilePath = memberProfilePath;
    }
}
