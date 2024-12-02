package com.be3c.sysmetic.domain.member.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeDeleteFailMessage {
    NOT_FOUND_INQUIRY("해당 문의를 찾을 수 없습니다."),
    NOT_FOUND_NOTICE("해당 공지를 찾을 수 없습니다.");

    private final String message;
}
