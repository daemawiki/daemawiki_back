package org.daemawiki.domain.document.component.facade;

import org.daemawiki.datetime.model.EditDateTime;
import org.daemawiki.domain.auth.type.GetDocumentType;
import org.daemawiki.domain.common.DefaultProfile;
import org.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import org.daemawiki.domain.document.model.DefaultDocument;
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
    private final GetDocumentType getDocumentType;
    private final DefaultProfile defaultProfile;

    public CreateDocumentFacadeImpl(GetDocumentType getDocumentType, DefaultProfile defaultProfile) {
        this.getDocumentType = getDocumentType;
        this.defaultProfile = defaultProfile;
    }

    @Override
    public Mono<DefaultDocument> create(SaveDocumentRequest request, User user) {
        UserDetailResponse userDetail = UserDetailResponse.create(user);

        return Mono.just(DefaultDocument.builder()
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
                .build());
    }

}
