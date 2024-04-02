package org.daemawiki.domain.common;

import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.user.model.User;

public interface UserFilter {

    void userPermissionAndDocumentVersionCheck(DefaultDocument document, String userEmail, Integer requestVersion);
    void userPermissionCheck(DefaultDocument document, String userEmail);
    void checkUserAndDocument(User user, DefaultDocument document, Integer version);

}
