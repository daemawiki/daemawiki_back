package org.daemawiki.domain.document.service;

import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.usecase.UpdateDocumentComponent;
import org.daemawiki.domain.user.dto.response.UserDetailResponse;
import org.daemawiki.domain.user.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UpdateDocumentEditorAndUpdatedDate implements UpdateDocumentComponent {

    @Override
    public void updateEditorAndUpdatedDate(DefaultDocument document, User user) {
        document.getEditor().setUpdatedUser(UserDetailResponse.create(user));
        document.getDateTime().setUpdated(LocalDateTime.now());
    }

}
