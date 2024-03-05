package com.example.daemawiki.domain.document.component;

import com.example.daemawiki.domain.content.model.Content;
import com.example.daemawiki.domain.document.component.facade.CreateDocumentFacade;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class CreateDocumentByUser {
    private final DocumentFacade documentFacade;
    private final CreateDocumentFacade createDocumentFacade;
    private final RevisionComponent revisionComponent;

    public CreateDocumentByUser(DocumentFacade documentFacade, CreateDocumentFacade createDocumentFacade, RevisionComponent revisionComponent) {
        this.documentFacade = documentFacade;
        this.createDocumentFacade = createDocumentFacade;
        this.revisionComponent = revisionComponent;
    }

    public Mono<DefaultDocument> execute(User user) {
        return documentFacade.saveDocument(createDocument(user))
                .flatMap(document -> revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                        .type(RevisionType.CREATE)
                        .documentId(document.getId())
                        .title(document.getTitle())
                        .build())
                        .thenReturn(document))
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

    private DefaultDocument createDocument(User user) {
        List<List<String>> groups = Arrays.asList(
                Arrays.asList("학생", user.getDetail().getGen() + "기", user.getName()),
                Arrays.asList("전공", user.getDetail().getMajor().getMajor())
        );

        return createDocumentFacade.execute(SaveDocumentRequest.builder()
                        .title(user.getName())
                        .type("student")
                        .content(Content.builder()
                                .index("1")
                                .title("개요")
                                .detail("회원가입을 통해 자동 생성된 문서입니다.")
                                .build())
                        .groups(groups)
                .build(), user);
    }

}
