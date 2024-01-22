package com.example.daemawiki.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    TEST(400, "TEST");

    private final int httpStatus;
    private final String message;
}
