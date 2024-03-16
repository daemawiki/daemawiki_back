package com.example.daemawiki.domain.document.component;

import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.user.dto.response.UserDetailResponse;
import com.example.daemawiki.domain.user.model.User;
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
