package org.daemawiki.domain.user.model.type.major;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MajorType {
    BACKEND("백엔드"),
    FRONTEND("프론트엔드"),
    IOS("ios"),
    ANDROID("안드로이드"),
    SCAMMER("사기꾼"),
    DEVOPS("데브옵스"),
    DESIGN("디자인"),
    SECURITY("보안"),
    GAME("게임"),
    EMBEDDED("임베디드"),
    SLAVE("노예"),
    AI("인공지능"),
    CLEANER("청소부"),
    GITOPS("깃옵스"),
    DBA("데이터베이스-관리자"),
    FLUTTER("플러터"),
    GUARD("지킴이"),
    FULLSTACK("풀스택");

    private final String major;
}
