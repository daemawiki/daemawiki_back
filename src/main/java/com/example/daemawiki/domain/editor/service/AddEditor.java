package com.example.daemawiki.domain.editor.service;

import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.editor.dto.AddEditorRequest;
import com.example.daemawiki.domain.editor.model.Editor;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h403.NoPermissionUserException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AddEditor {
    private final UserFacade userFacade;
    private final DocumentFacade documentFacade;
    private final DocumentRepository documentRepository;

    public AddEditor(UserFacade userFacade, DocumentFacade documentFacade, DocumentRepository documentRepository) {
        this.userFacade = userFacade;
        this.documentFacade = documentFacade;
        this.documentRepository = documentRepository;
    }

    public Mono<Void> execute(AddEditorRequest request, String documentId) {
        return Mono.zip(userFacade.currentUser(), documentFacade.findDocumentById(documentId))
                .flatMap(tuple -> {
                    if (tuple.getT2().getEditor().getCreatedUser().id().equals(tuple.getT1().getId())) {
                        return Mono.error(NoPermissionUserException.EXCEPTION);
                    }
                    return Mono.just(tuple.getT2());
                }).zipWith(userFacade.findByEmailNotNull(request.email()), (document, user2) -> {
                    document.getEditor().addEditor(Editor.builder()
                            .user(user2.getName())
                            .id(user2.getId())
                            .build());
                    return document;
                })
                .flatMap(documentRepository::save).then();
    }


}
