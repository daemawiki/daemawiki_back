package com.example.daemawiki.domain.content.service;

import com.example.daemawiki.domain.common.UserFilter;
import com.example.daemawiki.domain.content.dto.EditContentTableTitleRequest;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class EditContentTableTitle {
    private final DocumentFacade documentFacade;
    private final RevisionComponent revisionComponent;
    private final UserFacade userFacade;
    private final UserFilter userFilter;

    public EditContentTableTitle(DocumentFacade documentFacade, RevisionComponent revisionComponent, UserFacade userFacade, UserFilter userFilter) {
        this.documentFacade = documentFacade;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
        this.userFilter = userFilter;
    }

    public Mono<Void> execute(EditContentTableTitleRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId), (user, document) -> {
                    userFilter.userPermissionAndDocumentVersionCheck(document, user.getEmail(), request.version());
                    return document;
                })
                .map(document -> {
                    document.getContents()
                            .parallelStream()
                            .filter(c -> c.getIndex().equals(request.index()))
                            .findFirst()
                            .ifPresent(contents -> contents.setTitle(request.newTitle()));

                    document.increaseVersion();

                    return document;
                })
                .flatMap(document -> documentFacade.saveDocument(document)
                        .then(revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                                .type(RevisionType.UPDATE)
                                .documentId(documentId)
                                .title(document.getTitle())
                                .build()))
                );
    }

}
