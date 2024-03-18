package org.daemawiki.domain.editor.service;

import org.daemawiki.domain.document.component.facade.DocumentFacade;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.editor.dto.AddEditorRequest;
import org.daemawiki.domain.editor.model.Editor;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.service.facade.UserFacade;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class AddEditor {
    private final UserFacade userFacade;
    private final DocumentFacade documentFacade;

    public AddEditor(UserFacade userFacade, DocumentFacade documentFacade) {
        this.userFacade = userFacade;
        this.documentFacade = documentFacade;
    }

    public Mono<Void> execute(AddEditorRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId))
                .flatMap(this::checkPermission)
                .zipWith(userFacade.findByEmailNotNull(request.email()))
                .map(this::updateDocument)
                .flatMap(documentFacade::saveDocument)
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
