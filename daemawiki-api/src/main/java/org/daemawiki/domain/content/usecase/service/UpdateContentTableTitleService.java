package org.daemawiki.domain.content.usecase.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.content.dto.EditContentTableTitleRequest;
import org.daemawiki.domain.content.model.Content;
import org.daemawiki.domain.content.usecase.UpdateContentTableTitleUsecase;
import org.daemawiki.domain.document.application.GetDocumentPort;
import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.usecase.UpdateDocumentComponent;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.revision.usecase.CreateRevisionUsecase;
import org.daemawiki.domain.user.application.GetUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h404.ContentNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.util.function.Tuple2;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UpdateContentTableTitleService implements UpdateContentTableTitleUsecase {
    private final GetDocumentPort getDocumentPort;
    private final SaveDocumentPort saveDocumentPort;
    private final CreateRevisionUsecase createRevisionUsecase;
    private final GetUserPort getUserPort;
    private final UserFilter userFilter;
    private final Scheduler scheduler;
    private final UpdateDocumentComponent updateDocumentComponent;

    public UpdateContentTableTitleService(GetDocumentPort getDocumentPort, SaveDocumentPort saveDocumentPort, CreateRevisionUsecase createRevisionUsecase, GetUserPort getUserPort, UserFilter userFilter, Scheduler scheduler, UpdateDocumentComponent updateDocumentComponent) {
        this.getDocumentPort = getDocumentPort;
        this.saveDocumentPort = saveDocumentPort;
        this.createRevisionUsecase = createRevisionUsecase;
        this.getUserPort = getUserPort;
        this.userFilter = userFilter;
        this.scheduler = scheduler;
        this.updateDocumentComponent = updateDocumentComponent;
    }

    @Override
    public Mono<Void> update(EditContentTableTitleRequest request, String documentId) {
        return getUserPort.currentUser()
                .zipWith(getDocumentPort.getDocumentById(documentId))
                .flatMap(tuple -> checkPermissionAndUpdateDocument(tuple, request))
                .subscribeOn(scheduler)
                .flatMap(document -> saveDocumentPort.save(document)
                        .then(createRevision(document)));
    }

    private Mono<DefaultDocument> checkPermissionAndUpdateDocument(Tuple2<User, DefaultDocument> tuple, EditContentTableTitleRequest request) {
        DefaultDocument document = tuple.getT2();
        User user = tuple.getT1();

        userFilter.userPermissionAndDocumentVersionCheck(document, user.getEmail(), request.version());

        Map<String, Content> contentMap = document.getContents()
                .stream()
                .collect(Collectors.toMap(Content::getIndex, Function.identity()));

        Content content = contentMap.get(request.index());

        if (content != null){
            content.setTitle(request.newTitle());
        } else {
            return Mono.error(ContentNotFoundException.EXCEPTION);
        }

        document.increaseVersion();
        updateDocumentComponent.updateEditorAndUpdatedDate(document, user);

        return Mono.just(document);
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return createRevisionUsecase.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

}
