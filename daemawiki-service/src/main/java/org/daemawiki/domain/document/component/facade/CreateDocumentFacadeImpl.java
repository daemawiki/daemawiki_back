package org.daemawiki.domain.document.component.facade;

import org.daemawiki.config.DefaultProfileConfig;
import org.daemawiki.datetime.model.EditDateTime;
import org.daemawiki.domain.document_content.model.Content;
import org.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.type.DocumentType;
import org.daemawiki.domain.document_editor.model.DocumentEditor;
import org.daemawiki.domain.document_editor.model.Editor;
import org.daemawiki.domain.document_info.model.Info;
import org.daemawiki.domain.user.dto.response.UserDetailVo;
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
        UserDetailVo userDetail = UserDetailVo.create(user);

        return Mono.just(DefaultDocument.create(
                        request.title(),
                        DocumentType.valueOf(request.type().toUpperCase()),
                        EditDateTime.builder()
                                .created(LocalDateTime.now())
                                .updated(LocalDateTime.now())
                                .build(),
                        Info.builder()
                                .documentImage(defaultProfile.defaultDocumentImage())
                                .subTitle("")
                                .details(Lists.mutable.of())
                                .build(),
                        request.groups(),
                        DocumentEditor.builder()
                            .createdUser(userDetail)
                            .updatedUser(userDetail)
                            .canEdit(Collections.singletonList(Editor.of(user.getEmail(), user.getId())))
                            .build(),
                        Lists.mutable.of(Content.of("1", "개요", ""))
                ));
    }

}
