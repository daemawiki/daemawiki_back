package org.daemawiki.domain.editor.usecase.service;

import org.daemawiki.domain.document.application.GetDocumentPort;
import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.editor.dto.DeleteEditorRequest;
import org.daemawiki.domain.editor.usecase.RemoveDocumentEditorUsecase;
import org.daemawiki.domain.user.application.GetUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Objects;

@Service
public class RemoveDocumentEditorService implements RemoveDocumentEditorUsecase {
    private final GetUserPort getUserPort;
    private final GetDocumentPort getDocumentPort;
    private final SaveDocumentPort saveDocumentPort;

    public RemoveDocumentEditorService(GetUserPort getUserPort, GetDocumentPort getDocumentPort, SaveDocumentPort saveDocumentPort) {
        this.getUserPort = getUserPort;
        this.getDocumentPort = getDocumentPort;
        this.saveDocumentPort = saveDocumentPort;
    }

    @Override
    public Mono<Void> remove(DeleteEditorRequest request, String documentId) {
        return getUserPort.currentUser()
                .zipWith(getDocumentPort.getDocumentById(documentId))
                .flatMap(this::checkPermission)
                .flatMap(document -> updateDocument(document, request.userId()));
    }

    private Mono<Void> updateDocument(DefaultDocument document, String userId) {
        document.getEditor().getCanEdit().removeIf(e -> e.id().equals(userId));
        return saveDocumentPort.save(document)
                .then();
    }

    private Mono<DefaultDocument> checkPermission(Tuple2<User, DefaultDocument> tuple) {
        if (!Objects.equals(tuple.getT1().getId(), tuple.getT2().getEditor().getCreatedUser().id())) {
            return Mono.error(NoPermissionUserException.EXCEPTION);
        }
        return Mono.just(tuple.getT2());
    }

}
