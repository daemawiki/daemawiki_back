package com.example.daemawiki.domain.info.student.dto;

public record CreateStudentInfoRequest(
        String name,
        Integer age,
        Integer generation,
        String gender,
        String height,
        String weight,
        String birthday,
        String major,
        String nickname,
        String documentId
) {
}
