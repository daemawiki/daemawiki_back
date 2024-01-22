package com.example.daemawiki.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    PASSWORD_MISMATCH(401, "비밀번호가 일치하지 않습니다."),

    TEST(400, "TEST");

    private final int httpStatus;
    private final String message;
}
