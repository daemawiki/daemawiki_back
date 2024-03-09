package com.example.daemawiki.domain.user.service;

import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DeleteUser {
    private final UserFacade userFacade;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final RevisionComponent revisionComponent;

    public DeleteUser(UserFacade userFacade, UserRepository userRepository, DocumentRepository documentRepository, RevisionComponent revisionComponent) {
        this.userFacade = userFacade;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.revisionComponent = revisionComponent;
    }

    public Mono<Void> execute() {
        return userFacade.currentUser()
                .flatMap(user -> {
                    String documentId = user.getDocumentId();

                    return Mono.when(documentRepository.deleteById(documentId),
                            userRepository.delete(user))
                            .then(createRevision(documentId, user.getName()));
                });
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return revisionComponent.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.DELETE, document.getId(), document.getTitle()));
    }

}
