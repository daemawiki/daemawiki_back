package org.daemawiki.domain.document.service;

import org.daemawiki.domain.document.port.SaveDocumentPort;
import org.daemawiki.domain.document.component.facade.CreateDocumentFacade;
import org.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.usecase.CreateDocumentUsecase;
import org.daemawiki.domain.document_revision.component.CreateRevisionComponent;
import org.daemawiki.domain.document_revision.model.type.RevisionType;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h403.NoEditPermissionUserException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CreateDocumentService implements CreateDocumentUsecase {
    private final SaveDocumentPort saveDocumentPort;
    private final FindUserPort findUserPort;
    private final CreateDocumentFacade createDocumentFacade;
    private final CreateRevisionComponent createRevisionComponent;

    public CreateDocumentService(SaveDocumentPort saveDocumentPort, FindUserPort findUserPort, CreateDocumentFacade createDocumentFacade, CreateRevisionComponent createRevisionComponent) {
        this.saveDocumentPort = saveDocumentPort;
        this.findUserPort = findUserPort;
        this.createDocumentFacade = createDocumentFacade;
        this.createRevisionComponent = createRevisionComponent;
    }

    @Override
    public Mono<Void> create(SaveDocumentRequest request) {
        return findUserPort.currentUser()
                .filter(user -> !user.getIsBlocked())
                .switchIfEmpty(Mono.defer(() -> Mono.error(NoEditPermissionUserException.EXCEPTION)))
                .flatMap(user -> createDocumentFacade.create(request, user))
                .flatMap(this::saveDocumentAndCreateRevision)
                .onErrorMap(e -> e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION);
    }

    private Mono<Void> saveDocumentAndCreateRevision(DefaultDocument document) {
        return saveDocumentPort.save(document)
                .flatMap(this::createRevisionByDocument)
                .then();
    }

    @Override
    public Mono<DefaultDocument> createByUser(User user) {
        return createDocument(user)
                .flatMap(saveDocumentPort::save)
                .flatMap(this::createRevisionByDocument)
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

    private Mono<DefaultDocument> createRevisionByDocument(DefaultDocument document) {
        return createRevisionComponent.create(document, RevisionType.CREATE, null)
                .thenReturn(document);
    }

    private Mono<DefaultDocument> createDocument(User user) {
        List<List<String>> groups = List.of(
                List.of("학생", String.format("%s기", user.getDetail().getGen()), user.getName()),
                List.of("전공", user.getDetail().getMajor().getMajor())
        );

        return createDocumentFacade.create(SaveDocumentRequest
                .create(user.getName(),
                        "student",
                        groups), user);
    }

}
