package com.example.daemawiki.domain.document.component.facade;

import com.example.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.editor.model.DocumentEditor;
import com.example.daemawiki.domain.editor.model.Editor;
import com.example.daemawiki.domain.document.model.type.service.GetDocumentType;
import com.example.daemawiki.domain.user.dto.response.UserDetailResponse;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.global.datetime.model.EditDateTime;
import org.eclipse.collections.api.factory.Lists;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class CreateDocumentFacade {
    private final GetDocumentType getDocumentType;

    public CreateDocumentFacade(GetDocumentType getDocumentType) {
        this.getDocumentType = getDocumentType;
    }

    public DefaultDocument execute(SaveDocumentRequest request, User user) {
        UserDetailResponse userDetail = UserDetailResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profile(user.getProfile())
                .build();

        return DefaultDocument.builder()
                .title(request.title())
                .type(getDocumentType.execute(request.type()))
                .dateTime(EditDateTime.builder()
                        .created(LocalDateTime.now())
                        .updated(LocalDateTime.now())
                        .build())
                .documentEditor(DocumentEditor.builder()
                        .createdUser(userDetail)
                        .updatedUser(userDetail)
                        .canEdit(Lists.mutable.of(Editor.builder()
                                .email(user.getEmail())
                                .id(user.getId())
                                .build()))
                        .build())
                .content(Collections.singletonList(request.content()))
                .groups(request.groups())
                .build();
    }

}
