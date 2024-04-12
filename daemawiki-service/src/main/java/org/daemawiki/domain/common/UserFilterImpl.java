package org.daemawiki.domain.common;

import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h400.VersionMismatchException;
import org.daemawiki.exception.h403.NoEditPermissionUserException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserFilterImpl implements UserFilter {

    @Override
    public void userPermissionAndDocumentVersionCheck(DefaultDocument document, User user, Long requestVersion) {
        if (user.getRole() == User.Role.USER) {
            userPermissionCheck(document, user);
            if (!Objects.equals(document.getVersion(), requestVersion)) {
                throw VersionMismatchException.EXCEPTION;
            }
        }
    }

    @Override
    public void userPermissionCheck(DefaultDocument document, User user) {
        if (user.getRole() == User.Role.USER) {
            if (document.getEditor().hasEditPermission(user.getEmail()) || user.getIsBlocked()) {
                throw NoEditPermissionUserException.EXCEPTION;
            }
        }
    }

    @Override
    public void checkUserAndDocument(User user, DefaultDocument document, Long version) {
        userPermissionAndDocumentVersionCheck(document, user, version);
    }

}
