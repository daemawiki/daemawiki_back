package com.example.daemawiki.domain.document.component.facade;

import com.example.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.document.model.type.service.GetDocumentType;
import com.example.daemawiki.domain.editor.model.DocumentEditor;
import com.example.daemawiki.domain.editor.model.Editor;
import com.example.daemawiki.domain.file.model.DefaultProfile;
import com.example.daemawiki.domain.info.model.Info;
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
    private final DefaultProfile defaultProfile;

    public CreateDocumentFacade(GetDocumentType getDocumentType, DefaultProfile defaultProfile) {
        this.getDocumentType = getDocumentType;
        this.defaultProfile = defaultProfile;
    }

    public DefaultDocument execute(SaveDocumentRequest request, User user) {
        UserDetailResponse userDetail = UserDetailResponse.create(user);

        return DefaultDocument.builder()
                .title(request.title())
                .type(getDocumentType.execute(request.type().toLowerCase()))
                .dateTime(EditDateTime.builder()
                        .created(LocalDateTime.now())
                        .updated(LocalDateTime.now())
                        .build())
                .info(Info.builder()
                        .documentImage(defaultProfile.defaultDocumentImage())
                        .subTitle("")
                        .details(Lists.mutable.of())
                        .build())
                .documentEditor(DocumentEditor.builder()
                        .createdUser(userDetail)
                        .updatedUser(userDetail)
                        .canEdit(Collections.singletonList(Editor.create(user.getEmail(), user.getId())))
                        .build())
                .content(Lists.mutable.of())
                .groups(request.groups())
                .build();
    }

}
