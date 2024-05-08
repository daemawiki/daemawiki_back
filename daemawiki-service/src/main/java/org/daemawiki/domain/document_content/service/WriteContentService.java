package org.daemawiki.domain.document_content.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.document.port.FindDocumentPort;
import org.daemawiki.domain.document.port.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.component.UpdateDocumentComponent;
import org.daemawiki.domain.document_content.dto.WriteContentRequest;
import org.daemawiki.domain.document_content.model.Content;
import org.daemawiki.domain.document_content.usecase.WriteContentUsecase;
import org.daemawiki.domain.document_revision.component.CreateRevisionComponent;
import org.daemawiki.domain.document_revision.model.type.RevisionType;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h400.VersionMismatchException;
import org.daemawiki.exception.h403.NoEditPermissionUserException;
import org.daemawiki.exception.h404.ContentNotFoundException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WriteContentService implements WriteContentUsecase {
    private final SaveDocumentPort saveDocumentPort;
    private final FindDocumentPort findDocumentPort;
    private final FindUserPort findUserPort;
    private final UserFilter userFilter;
    private final UpdateDocumentComponent updateDocumentComponent;
    private final CreateRevisionComponent createRevisionComponent;

    public WriteContentService(SaveDocumentPort saveDocumentPort, FindDocumentPort findDocumentPort, FindUserPort findUserPort, UserFilter userFilter, UpdateDocumentComponent updateDocumentComponent, CreateRevisionComponent createRevisionComponent) {
        this.saveDocumentPort = saveDocumentPort;
        this.findDocumentPort = findDocumentPort;
        this.findUserPort = findUserPort;
        this.userFilter = userFilter;
        this.updateDocumentComponent = updateDocumentComponent;
        this.createRevisionComponent = createRevisionComponent;
    }

    @Override
    public Mono<Void> write(WriteContentRequest request, String documentId) {
        return findUserPort.currentUser()
                .zipWith(findDocumentPort.findById(documentId))
                .flatMap(tuple -> checkPermissionAndWriteContent(tuple, request))
                .flatMap(this::saveDocumentAndCreateRevision)
                .onErrorMap(this::mapException);
    }

    private Mono<Void> saveDocumentAndCreateRevision(DefaultDocument document) {
        return saveDocumentPort.save(document)
                .then(createRevisionComponent.create(document, RevisionType.UPDATE));
    }

    private Mono<DefaultDocument> checkPermissionAndWriteContent(Tuple2<User, DefaultDocument> tuple, WriteContentRequest request) {
        DefaultDocument document = tuple.getT2();
        User user = tuple.getT1();

        userFilter.userPermissionAndDocumentVersionCheck(document, user, request.version());

        Map<String, Content> contentsMap = document.getContents().stream()
                .collect(Collectors.toMap(Content::getIndex, Function.identity()));

        if (!contentsMap.containsKey(request.index())) {
            return Mono.error(ContentNotFoundException.EXCEPTION);
        }

        Content content = contentsMap.get(request.index());
        content.changeDetail(request.content());
        setDocument(document, user);

        return Mono.just(document);
    }

    private void setDocument(DefaultDocument document, User user) {
        updateDocumentComponent.updateEditorAndUpdatedDate(document, user);
        document.increaseVersion();
    }

    private Throwable mapException(Throwable e) {
        return (e instanceof ContentNotFoundException || e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException) ? e : ExecuteFailedException.EXCEPTION;
    }

}
