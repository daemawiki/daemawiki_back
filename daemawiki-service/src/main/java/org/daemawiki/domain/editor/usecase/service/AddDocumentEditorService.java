package org.daemawiki.domain.editor.usecase.service;

import org.daemawiki.domain.document.application.GetDocumentPort;
import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.editor.dto.AddEditorRequest;
import org.daemawiki.domain.editor.model.Editor;
import org.daemawiki.domain.editor.usecase.AddDocumentEditorUsecase;
import org.daemawiki.domain.user.application.GetUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.daemawiki.exception.h404.UserNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class AddDocumentEditorService implements AddDocumentEditorUsecase {
    private final GetUserPort getUserPort;
    private final SaveDocumentPort saveDocumentPort;
    private final GetDocumentPort getDocumentPort;

    public AddDocumentEditorService(GetUserPort getUserPort, SaveDocumentPort saveDocumentPort, GetDocumentPort getDocumentPort) {
        this.getUserPort = getUserPort;
        this.saveDocumentPort = saveDocumentPort;
        this.getDocumentPort = getDocumentPort;
    }

    @Override
    public Mono<Void> add(AddEditorRequest request, String documentId) {
        return getUserPort.currentUser()
                .zipWith(getDocumentPort.getDocumentById(documentId))
                .flatMap(this::checkPermission)
                .zipWith(getUserPort.findByEmail(request.email())
                        .switchIfEmpty(Mono.error(UserNotFoundException.EXCEPTION)))
                .map(this::updateDocument)
                .flatMap(saveDocumentPort::save)
                .then();
    }

    private DefaultDocument updateDocument(Tuple2<DefaultDocument, User> tuple) {
        DefaultDocument document = tuple.getT1();
        User user = tuple.getT2();

        document.getEditor().addEditor(Editor.create(user.getName(), user.getId()));

        return document;
    }

    private Mono<DefaultDocument> checkPermission(Tuple2<User, DefaultDocument> tuple) {
        if (!tuple.getT2().getEditor().getCreatedUser().id().equals(tuple.getT1().getId())) {
            return Mono.error(NoPermissionUserException.EXCEPTION);
        }

        return Mono.just(tuple.getT2());
    }

}
