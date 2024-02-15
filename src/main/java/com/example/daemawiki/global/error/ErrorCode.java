package com.example.daemawiki.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_DATA(400, "잘못된 데이터입니다."),
    INVALID_TOKEN(400, "유효하지 않은 토큰입니다."),
    VERSION_MISMATCH(400, "문서의 버전이 일치하지 않습니다."),

    PASSWORD_MISMATCH(401, "비밀번호가 일치하지 않습니다."),

    UNVERIFIED_EMAIL(403, "이메일 인증을 하지 않은 사용자입니다."),

    USER_NOT_FOUND(404, "해당 이메일로 가입된 유저를 찾지 못했습니다."),
    DOCUMENT_NOT_FOUND(404, "해당 문서를 찾지 못했습니다."),
    DOCUMENT_GROUP_NOT_FOUND(404, "해당 분류 그룹을 찾지 못했습니다."),
    STUDENT_INFO_NOT_FOUND(404, "해당 학생 정보를 찾지 못했습니다."),
    FILE_NOT_FOUND(404, "해당 id로 파일을 찾지 못했습니다."),

    ALREADY_EXISTS_EMAIL(409, "이 이메일을 사용 중인 유저가 존재합니다."),

    MAIL_CONFIRM_FAILED(500, "메일 전송 실패 (서버 오류)"),
    TOKEN_REISSUE_FAILED(500, "토큰 재발급 실패"),
    IMAGE_UPLOAD_FAILED(500, "이미지 생성 실패ㅋ"),


    TEST(400, "TEST");

    private final int httpStatus;
    private final String message;
}
