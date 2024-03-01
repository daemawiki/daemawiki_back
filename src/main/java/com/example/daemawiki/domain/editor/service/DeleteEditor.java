package com.example.daemawiki.domain.editor.service;

import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.editor.dto.DeleteEditorRequest;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h403.NoPermissionUserException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class DeleteEditor {
    private final UserFacade userFacade;
    private final DocumentFacade documentFacade;
    private final DocumentRepository documentRepository;

    public DeleteEditor(UserFacade userFacade, DocumentFacade documentFacade, DocumentRepository documentRepository) {
        this.userFacade = userFacade;
        this.documentFacade = documentFacade;
        this.documentRepository = documentRepository;
    }

    public Mono<Void> execute(DeleteEditorRequest request, String documentId) {
        return userFacade.currentUser()
                .flatMap(user -> documentFacade.findDocumentById(documentId)
                        .filter(document -> Objects.equals(user.getId(), document.getEditor().getCreatedUser().id()))
                        .switchIfEmpty(Mono.error(NoPermissionUserException.EXCEPTION))
                        .flatMap(document -> {
                            document.getEditor().getCanEdit().removeIf(e -> e.id().equals(request.userId()));
                            return documentRepository.save(document);
                        }))
                .then();
    }

}
