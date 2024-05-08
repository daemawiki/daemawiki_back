package org.daemawiki.domain.document_editor.service;

import org.daemawiki.domain.document.port.FindDocumentPort;
import org.daemawiki.domain.document.port.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document_editor.dto.DeleteEditorRequest;
import org.daemawiki.domain.document_editor.usecase.RemoveDocumentEditorUsecase;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Objects;

@Service
public class RemoveDocumentEditorService implements RemoveDocumentEditorUsecase {
    private final FindUserPort findUserPort;
    private final FindDocumentPort findDocumentPort;
    private final SaveDocumentPort saveDocumentPort;

    public RemoveDocumentEditorService(FindUserPort findUserPort, FindDocumentPort findDocumentPort, SaveDocumentPort saveDocumentPort) {
        this.findUserPort = findUserPort;
        this.findDocumentPort = findDocumentPort;
        this.saveDocumentPort = saveDocumentPort;
    }

    @Override
    public Mono<Void> remove(DeleteEditorRequest request, String documentId) {
        return findUserPort.currentUser()
                .zipWith(findDocumentPort.findById(documentId))
                .flatMap(this::checkPermission)
                .flatMap(document -> updateDocument(document, request.userId()));
    }

    private Mono<Void> updateDocument(DefaultDocument document, String userId) {
        document.getEditor().getCanEdit().removeIf(e -> e.id().equals(userId));
        return saveDocumentPort.save(document)
                .then();
    }

    private Mono<DefaultDocument> checkPermission(Tuple2<User, DefaultDocument> tuple) {
        if (!Objects.equals(tuple.getT1().getId(), tuple.getT2().getEditor().getCreatedUser().id()) || tuple.getT1().getIsBlocked()) {
            return Mono.error(NoPermissionUserException.EXCEPTION);
        }
        return Mono.just(tuple.getT2());
    }

}
