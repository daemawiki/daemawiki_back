package com.example.daemawiki.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    PASSWORD_MISMATCH(401, "비밀번호가 일치하지 않습니다."),

    ALREADY_EXISTS_EMAIL(409, "이 이메일을 사용 중인 유저가 존재합니다."),

    UNVERIFIED_EMAIL(403, "이메일 인증을 하지 않은 사용자입니다."),

    USER_NOT_FOUND(404, "해당 이메일로 가입된 유저를 찾지 못했습니다."),

    MAIL_CONFIRM_FAILED(500, "메일 전송 실패 (서버 오류)"),


    TEST(400, "TEST");

    private final int httpStatus;
    private final String message;
}
