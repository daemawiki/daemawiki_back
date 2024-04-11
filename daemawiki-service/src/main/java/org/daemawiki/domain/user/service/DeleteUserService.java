package org.daemawiki.domain.user.service;

import org.daemawiki.domain.document.application.DeleteDocumentPort;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.revision.usecase.CreateRevisionUsecase;
import org.daemawiki.domain.user.application.DeleteUserPort;
import org.daemawiki.domain.user.application.FindUserPort;
import org.daemawiki.domain.user.usecase.DeleteUserUsecase;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DeleteUserService implements DeleteUserUsecase {
    private final FindUserPort findUserPort;
    private final DeleteUserPort deleteUserPort;
    private final DeleteDocumentPort deleteDocumentPort;
    private final CreateRevisionUsecase createRevisionUsecase;

    public DeleteUserService(FindUserPort findUserPort, DeleteUserPort deleteUserPort, DeleteDocumentPort deleteDocumentPort, CreateRevisionUsecase createRevisionUsecase) {
        this.findUserPort = findUserPort;
        this.deleteUserPort = deleteUserPort;
        this.deleteDocumentPort = deleteDocumentPort;
        this.createRevisionUsecase = createRevisionUsecase;
    }

    @Override
    public Mono<Void> deleteCurrentUser() {
        return findUserPort.currentUser()
                .flatMap(user -> {
                    String documentId = user.getDocumentId();

                    return Mono.when(deleteDocumentPort.deleteById(documentId),
                            deleteUserPort.delete(user))
                            .then(createRevision(documentId, user.getName()));
                });
    }

    private Mono<Void> createRevision(String documentId, String name) {
        return createRevisionUsecase.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.DELETE, documentId, name));
    }

}
