package org.daemawiki.domain.document_content.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.document.port.FindDocumentPort;
import org.daemawiki.domain.document.port.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.component.UpdateDocumentComponent;
import org.daemawiki.domain.document_content.dto.EditContentTableTitleRequest;
import org.daemawiki.domain.document_content.model.Content;
import org.daemawiki.domain.document_content.usecase.UpdateContentTableTitleUsecase;
import org.daemawiki.domain.document_revision.component.CreateRevisionComponent;
import org.daemawiki.domain.document_revision.model.type.RevisionType;
import org.daemawiki.domain.user.port.FindUserPort;
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
    private final FindUserPort findUserPort;
    private final UserFilter userFilter;
    private final Scheduler scheduler;
    private final UpdateDocumentComponent updateDocumentComponent;
    private final CreateRevisionComponent createRevisionComponent;

    public UpdateContentTableTitleService(FindDocumentPort findDocumentPort, SaveDocumentPort saveDocumentPort, FindUserPort findUserPort, UserFilter userFilter, Scheduler scheduler, UpdateDocumentComponent updateDocumentComponent, CreateRevisionComponent createRevisionComponent) {
        this.findDocumentPort = findDocumentPort;
        this.saveDocumentPort = saveDocumentPort;
        this.findUserPort = findUserPort;
        this.userFilter = userFilter;
        this.scheduler = scheduler;
        this.updateDocumentComponent = updateDocumentComponent;
        this.createRevisionComponent = createRevisionComponent;
    }

    @Override
    public Mono<Void> update(EditContentTableTitleRequest request, String documentId) {
        return findUserPort.currentUser()
                .zipWith(findDocumentPort.findById(documentId))
                .flatMap(tuple -> checkPermissionAndUpdateDocument(tuple, request))
                .subscribeOn(scheduler)
                .flatMap(this::saveDocumentAndCreateRevision);
    }

    private Mono<Void> saveDocumentAndCreateRevision(DefaultDocument document) {
        return saveDocumentPort.save(document)
                .then(createRevisionComponent.create(document, RevisionType.UPDATE));
    }

    private Mono<DefaultDocument> checkPermissionAndUpdateDocument(Tuple2<User, DefaultDocument> tuple, EditContentTableTitleRequest request) {
        DefaultDocument document = tuple.getT2();
        User user = tuple.getT1();

        userFilter.userPermissionAndDocumentVersionCheck(document, user, request.version());

        Map<String, Content> contentMap = document.getContents()
                .stream()
                .collect(Collectors.toMap(Content::getIndex, Function.identity()));

        Content content = contentMap.get(request.index());

        if (content == null){
            return Mono.error(ContentNotFoundException.EXCEPTION);
        }

        content.setTitle(request.newTitle());

        document.increaseVersion();
        updateDocumentComponent.updateEditorAndUpdatedDate(document, user);

        return Mono.just(document);
    }

}
