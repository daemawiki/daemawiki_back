package com.example.daemawiki.domain.user.model.type.component;

import com.example.daemawiki.domain.user.model.type.MajorType;
import org.springframework.stereotype.Component;

@Component
public class GetMajorType {
    /*
        💀
    */
    public MajorType execute(String major) {
        return switch (major.toLowerCase()) {
            case "backend" -> MajorType.BACKEND;
            case "frontend" -> MajorType.FRONTEND;
            case "app" -> MajorType.APP;
            case "teacher" -> MajorType.SCAMMER;
            default -> MajorType.CLEANER;
        };
    }

}
