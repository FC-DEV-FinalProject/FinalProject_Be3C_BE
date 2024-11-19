package com.be3c.sysmetic.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum Code {
    USING_STATE("US001"),
    NOT_USING_STATE("US002");

    private String code;
}
