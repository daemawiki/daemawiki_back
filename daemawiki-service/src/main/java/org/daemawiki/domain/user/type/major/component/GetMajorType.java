package org.daemawiki.domain.user.type.major.component;

import org.daemawiki.domain.user.model.type.major.MajorType;
import org.springframework.stereotype.Component;

@Component
public class GetMajorType {

    public MajorType execute(String major) {
        return switch (major) {
            case "backend" -> MajorType.BACKEND;
            case "frontend" -> MajorType.FRONTEND;
            case "ios" -> MajorType.IOS;
            case "android" -> MajorType.ANDROID;
            case "teacher" -> MajorType.SCAMMER;
            case "student" -> MajorType.SLAVE;
            case "devops" -> MajorType.DEVOPS;
            case "gitops" -> MajorType.GITOPS;
            case "ai" -> MajorType.AI;
            case "design" -> MajorType.DESIGN;
            case "security" -> MajorType.SECURITY;
            case "game" -> MajorType.GAME;
            case "embedded" -> MajorType.EMBEDDED;
            case "dba" -> MajorType.DBA;
            case "fullstack" -> MajorType.FULLSTACK;
            case "flutter" -> MajorType.FLUTTER;

            case null, default -> MajorType.CLEANER;
        };
    }

}
