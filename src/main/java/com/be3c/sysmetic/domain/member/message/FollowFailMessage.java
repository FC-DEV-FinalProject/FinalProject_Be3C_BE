package com.be3c.sysmetic.domain.member.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FollowFailMessage {
    NOT_FOUND_USER("요청한 유저를 찾을 수 없습니다."),
    NOT_FOUND_FOLDER("요청한 폴더를 찾을 수 없습니다."),
    NOT_FOUND_STRATEGY("해당 관심 전략을 찾을 수 없습니다.");

    private final String message;
}
