package org.daemawiki.domain.common;

import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.user.model.User;

public interface UserFilter {

    void userPermissionAndDocumentVersionCheck(DefaultDocument document, User user, Long requestVersion);
    void userPermissionCheck(DefaultDocument document, User user);
    void checkUserAndDocument(User user, DefaultDocument document, Long version);

}
