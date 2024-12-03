package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 수정 요청 DTO")
public class NoticeModifyRequestDto {

//    @Schema(description = "공지사항 수정 화면에 들어온 시간", example = "2024-11-23 21:16:19.274999")
////    @NotNull
//    private LocalDateTime modifyInModifyPageTime;

    @Schema(description = "공지사항 제목", example = "새로운 공지사항 제목")
    @Size(max = 100)
    @NotBlank
    private String noticeTitle;

    @Schema(description = "공지사항 내용", example = "공지사항 내용 예시입니다.")
    @Size(max = 1000)
    @NotBlank
    private String noticeContent;

    @Schema(description = "첨부 파일 존재 여부", example = "true")
    @NotNull
    private Boolean fileExists;

    @Schema(description = "이미지 파일 존재 여부", example = "true")
    @NotNull
    private Boolean imageExists;

    @Schema(description = "공개 여부", example = "true")
    @NotNull
    private Boolean isOpen;

    @Schema(description = "지금 공지사항에 있는 파일의 id와 존재 유무 Map의 리스트", example = "[]")
    private List<NoticeExistFileImageRequestDto> existFileDtoList;

    @Schema(description = "지금 공지사항에 있는 이미지의 id와 존재 유무 Map의 리스트", example = "[]")
    private List<NoticeExistFileImageRequestDto> existImageDtoList;
}
