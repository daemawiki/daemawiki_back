package org.daemawiki.domain.document.usecase;

import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.user.model.User;

public interface UpdateDocumentComponent {

    void updateEditorAndUpdatedDate(DefaultDocument document, User user);
}
