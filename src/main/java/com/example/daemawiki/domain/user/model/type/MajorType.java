package com.example.daemawiki.domain.user.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MajorType {
    BACKEND("백엔드"),
    FRONTEND("프론트엔드"),
    APP("앱-개발자"),
    SCAMMER("사기꾼"),
    DEVOPS("데브옵스"),
    DESIGN("디자인"),
    SECURITY("보안"),
    GAME("게임-개발"),
    EMBEDDED("임베디드"),
    SLAVE("노예"),
    AI("인공지능-개발"),
    CLEANER("청소부"),
    GITOPS("깃옵스"),
    DBA("데이터베이스-관리자"),
    FULLSTACK("풀스택-개발자");

    private final String major;
}
