package org.daemawiki.domain.document.usecase.service;

import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.component.facade.CreateDocumentFacade;
import org.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.usecase.CreateDocumentUsecase;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.revision.usecase.CreateRevisionUsecase;
import org.daemawiki.domain.user.application.GetUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CreateDocumentService implements CreateDocumentUsecase {
    private final SaveDocumentPort saveDocumentPort;
    private final GetUserPort getUserPort;
    private final CreateDocumentFacade createDocumentFacade;
    private final CreateRevisionUsecase createRevisionUsecase;

    public CreateDocumentService(SaveDocumentPort saveDocumentPort, GetUserPort getUserPort, CreateDocumentFacade createDocumentFacade, CreateRevisionUsecase createRevisionUsecase) {
        this.saveDocumentPort = saveDocumentPort;
        this.getUserPort = getUserPort;
        this.createDocumentFacade = createDocumentFacade;
        this.createRevisionUsecase = createRevisionUsecase;
    }

    @Override
    public Mono<Void> create(SaveDocumentRequest request) {
        return getUserPort.currentUser()
                .flatMap(user -> createDocumentFacade.create(request, user))
                .flatMap(document -> saveDocumentPort.save(document)
                        .then(createRevision(document)))
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

    @Override
    public Mono<DefaultDocument> createByUser(User user) {
        return createDocument(user)
                .flatMap(saveDocumentPort::save)
                .flatMap(document -> createRevision(document)
                        .thenReturn(document))
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

    private Mono<DefaultDocument> createDocument(User user) {
        List<List<String>> groups = List.of(
                List.of("학생", user.getDetail().getGen() + "기", user.getName()),
                List.of("전공", user.getDetail().getMajor().getMajor())
        );

        return createDocumentFacade.create(SaveDocumentRequest
                .create(user.getName(),
                        "student",
                        groups), user);
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return createRevisionUsecase.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.CREATE, document.getId(), document.getTitle()));
    }

}
