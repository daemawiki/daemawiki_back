package org.daemawiki.domain.document.component.facade;

import org.daemawiki.config.DefaultProfileConfig;
import org.daemawiki.datetime.model.EditDateTime;
import org.daemawiki.domain.content.model.Content;
import org.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.type.DocumentType;
import org.daemawiki.domain.editor.model.DocumentEditor;
import org.daemawiki.domain.editor.model.Editor;
import org.daemawiki.domain.info.model.Info;
import org.daemawiki.domain.user.dto.response.UserDetailResponse;
import org.daemawiki.domain.user.model.User;
import org.eclipse.collections.api.factory.Lists;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class CreateDocumentFacadeImpl implements CreateDocumentFacade{
    private final DefaultProfileConfig defaultProfile;

    public CreateDocumentFacadeImpl(DefaultProfileConfig defaultProfile) {
        this.defaultProfile = defaultProfile;
    }

    @Override
    public Mono<DefaultDocument> create(SaveDocumentRequest request, User user) {
        UserDetailResponse userDetail = UserDetailResponse.create(user);

        return Mono.just(DefaultDocument.builder()
                .title(request.title())
                .type(DocumentType.valueOf(request.type().toUpperCase()))
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
                .content(Lists.mutable.of(Content.create("1", "개요", user.getName())))
                .groups(request.groups())
                .build());
    }

}
