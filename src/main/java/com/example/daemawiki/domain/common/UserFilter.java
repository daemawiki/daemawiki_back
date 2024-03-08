package com.example.daemawiki.domain.common;

import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.global.exception.h400.VersionMismatchException;
import com.example.daemawiki.global.exception.h403.NoEditPermissionUserException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserFilter {

    public void userPermissionAndDocumentVersionCheck(DefaultDocument document, String userEmail, Integer requestVersion) {
        if (document.getEditor().hasEditPermission(userEmail)) {
            throw NoEditPermissionUserException.EXCEPTION;
        }
        if (!Objects.equals(document.getVersion(), requestVersion)) {
            throw VersionMismatchException.EXCEPTION;
        }
    }

    public DefaultDocument checkUserAndDocument(User user, DefaultDocument document, Integer version) {
        userPermissionAndDocumentVersionCheck(document, user.getEmail(), version);
        return document;
    }

}
