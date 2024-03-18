package org.daemawiki.domain.editor.service;

import org.daemawiki.domain.document.component.facade.DocumentFacade;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.editor.dto.DeleteEditorRequest;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.service.facade.UserFacade;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Objects;

@Service
public class DeleteEditor {
    private final UserFacade userFacade;
    private final DocumentFacade documentFacade;

    public DeleteEditor(UserFacade userFacade, DocumentFacade documentFacade) {
        this.userFacade = userFacade;
        this.documentFacade = documentFacade;
    }

    public Mono<Void> execute(DeleteEditorRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId))
                .flatMap(this::checkPermission)
                .flatMap(document -> updateDocument(document, request.userId()));
    }

    private Mono<Void> updateDocument(DefaultDocument document, String userId) {
        document.getEditor().getCanEdit().removeIf(e -> e.id().equals(userId));
        return documentFacade.saveDocument(document)
                .then();
    }

    private Mono<DefaultDocument> checkPermission(Tuple2<User, DefaultDocument> tuple) {
        if (!Objects.equals(tuple.getT1().getId(), tuple.getT2().getEditor().getCreatedUser().id())) {
            return Mono.error(NoPermissionUserException.EXCEPTION);
        }
        return Mono.just(tuple.getT2());
    }

}
