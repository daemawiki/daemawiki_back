package org.daemawiki.domain.document_editor.service;

import org.daemawiki.domain.document.port.FindDocumentPort;
import org.daemawiki.domain.document.port.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document_editor.dto.AddEditorRequest;
import org.daemawiki.domain.document_editor.model.Editor;
import org.daemawiki.domain.document_editor.usecase.AddDocumentEditorUsecase;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.daemawiki.exception.h404.UserNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class AddDocumentEditorService implements AddDocumentEditorUsecase {
    private final FindUserPort findUserPort;
    private final SaveDocumentPort saveDocumentPort;
    private final FindDocumentPort findDocumentPort;

    public AddDocumentEditorService(FindUserPort findUserPort, SaveDocumentPort saveDocumentPort, FindDocumentPort findDocumentPort) {
        this.findUserPort = findUserPort;
        this.saveDocumentPort = saveDocumentPort;
        this.findDocumentPort = findDocumentPort;
    }

    @Override
    public Mono<Void> add(AddEditorRequest request, String documentId) {
        return findUserPort.currentUser()
                .zipWith(findDocumentPort.findById(documentId))
                .flatMap(this::checkPermission)
                .zipWith(findUser(request))
                .map(this::updateDocument)
                .flatMap(saveDocumentPort::save)
                .then();
    }

    private Mono<User> findUser(AddEditorRequest request) {
        return findUserPort.findByEmail(request.email())
                .switchIfEmpty(Mono.defer(() -> Mono.error(UserNotFoundException.EXCEPTION)));
    }

    private DefaultDocument updateDocument(Tuple2<DefaultDocument, User> tuple) {
        DefaultDocument document = tuple.getT1();
        User user = tuple.getT2();

        document.getEditor().addEditor(Editor.create(user.getName(), user.getId()));

        return document;
    }

    private Mono<DefaultDocument> checkPermission(Tuple2<User, DefaultDocument> tuple) {
        if (!tuple.getT2().getEditor().getCreatedUser().id().equals(tuple.getT1().getId()) || tuple.getT1().getIsBlocked()) {
            return Mono.error(NoPermissionUserException.EXCEPTION);
        }

        return Mono.just(tuple.getT2());
    }

}
