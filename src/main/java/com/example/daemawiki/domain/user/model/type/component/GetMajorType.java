package com.example.daemawiki.domain.user.model.type.component;

import com.example.daemawiki.domain.user.model.type.MajorType;
import org.springframework.stereotype.Component;

@Component
public class GetMajorType {

    public MajorType execute(String major) {
        return switch (major) {
            case "backend" -> MajorType.BACKEND;
            case "frontend" -> MajorType.FRONTEND;
            case "app" -> MajorType.APP;
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

            case null, default -> MajorType.CLEANER;
        };
    }

}
