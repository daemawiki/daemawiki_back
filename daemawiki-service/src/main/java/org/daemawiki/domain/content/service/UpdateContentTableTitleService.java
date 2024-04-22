package org.daemawiki.domain.content.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.content.dto.EditContentTableTitleRequest;
import org.daemawiki.domain.content.model.Content;
import org.daemawiki.domain.content.usecase.UpdateContentTableTitleUsecase;
import org.daemawiki.domain.document.application.FindDocumentPort;
import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.usecase.UpdateDocumentComponent;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.revision.usecase.CreateRevisionUsecase;
import org.daemawiki.domain.user.application.FindUserPort;
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
    private final FindDocumentPort findDocumentPort;
    private final SaveDocumentPort saveDocumentPort;
    private final CreateRevisionUsecase createRevisionUsecase;
    private final FindUserPort findUserPort;
    private final UserFilter userFilter;
    private final Scheduler scheduler;
    private final UpdateDocumentComponent updateDocumentComponent;

    public UpdateContentTableTitleService(FindDocumentPort findDocumentPort, SaveDocumentPort saveDocumentPort, CreateRevisionUsecase createRevisionUsecase, FindUserPort findUserPort, UserFilter userFilter, Scheduler scheduler, UpdateDocumentComponent updateDocumentComponent) {
        this.findDocumentPort = findDocumentPort;
        this.saveDocumentPort = saveDocumentPort;
        this.createRevisionUsecase = createRevisionUsecase;
        this.findUserPort = findUserPort;
        this.userFilter = userFilter;
        this.scheduler = scheduler;
        this.updateDocumentComponent = updateDocumentComponent;
    }

    @Override
    public Mono<Void> update(EditContentTableTitleRequest request, String documentId) {
        return findUserPort.currentUser()
                .zipWith(findDocumentPort.getDocumentById(documentId))
                .flatMap(tuple -> checkPermissionAndUpdateDocument(tuple, request))
                .subscribeOn(scheduler)
                .flatMap(this::saveDocumentAndCreateRevision);
    }

    private Mono<Void> saveDocumentAndCreateRevision(DefaultDocument document) {
        return saveDocumentPort.save(document)
                .then(createRevision(document));
    }

    private Mono<DefaultDocument> checkPermissionAndUpdateDocument(Tuple2<User, DefaultDocument> tuple, EditContentTableTitleRequest request) {
        DefaultDocument document = tuple.getT2();
        User user = tuple.getT1();

        userFilter.userPermissionAndDocumentVersionCheck(document, user, request.version());

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
