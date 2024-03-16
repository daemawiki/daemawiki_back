package com.example.daemawiki.domain.document.component;

import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.user.model.User;

public interface UpdateDocumentComponent {

    void updateEditorAndUpdatedDate(DefaultDocument document, User user);
}
