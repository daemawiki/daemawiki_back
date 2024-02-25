package com.example.daemawiki.domain.document.component.facade;

import com.example.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.document.model.DocumentEditor;
import com.example.daemawiki.domain.document.model.type.service.GetDocumentType;
import com.example.daemawiki.domain.user.dto.UserDetailResponse;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.global.datetime.model.EditDateTime;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
                        .build())
                .content(request.content())
                .groups(request.groups())
                .build();
    }

}
