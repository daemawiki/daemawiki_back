package com.example.daemawiki.domain.editor.service;

import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.editor.dto.AddEditorRequest;
import com.example.daemawiki.domain.editor.model.Editor;
import com.example.daemawiki.domain.user.model.User;
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
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId))
                .flatMap(tuple -> {
                    DefaultDocument document = tuple.getT2();
                    User user = tuple.getT1();

                    if (!document.getEditor().getCreatedUser().id().equals(user.getId())) {
                        return Mono.error(NoPermissionUserException.EXCEPTION);
                    }
                    return Mono.just(document);
                })
                .zipWith(userFacade.findByEmailNotNull(request.email()))
                .map(tuple -> {
                    DefaultDocument document = tuple.getT1();
                    User user = tuple.getT2();

                    document.getEditor().addEditor(Editor.create(user.getName(), user.getId()));
                    return document;
                })
                .flatMap(documentRepository::save).then();
    }

}
