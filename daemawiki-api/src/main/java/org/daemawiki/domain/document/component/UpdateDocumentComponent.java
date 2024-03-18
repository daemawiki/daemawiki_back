package org.daemawiki.domain.document.component;

import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.user.model.User;

public interface UpdateDocumentComponent {

    void updateEditorAndUpdatedDate(DefaultDocument document, User user);
}
