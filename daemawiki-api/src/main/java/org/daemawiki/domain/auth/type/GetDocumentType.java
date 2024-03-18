package org.daemawiki.domain.auth.type;

import org.daemawiki.domain.document.model.type.DocumentType;
import org.springframework.stereotype.Component;

@Component
public class GetDocumentType {

    public DocumentType execute(String type) {
        return switch (type) {
            case "student" -> DocumentType.STUDENT;
            case "teacher" -> DocumentType.TEACHER;
            case "club" -> DocumentType.CLUB;
            case "gen" -> DocumentType.GEN;
            case "major" -> DocumentType.MAJOR;
            case "main" -> DocumentType.MAIN;

            case null, default -> DocumentType.DEFAULT;
        };
    }

}
