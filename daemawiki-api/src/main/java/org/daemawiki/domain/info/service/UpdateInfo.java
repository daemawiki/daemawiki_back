package org.daemawiki.domain.info.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.document.component.facade.DocumentFacade;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.repository.DocumentRepository;
import org.daemawiki.domain.info.dto.UpdateInfoRequest;
import org.daemawiki.domain.info.model.Detail;
import org.daemawiki.domain.revision.component.RevisionComponent;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.user.dto.response.UserDetailResponse;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.service.facade.UserFacade;
import org.daemawiki.exception.h400.VersionMismatchException;
import org.daemawiki.exception.h403.NoEditPermissionUserException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

@Service
public class UpdateInfo {
    private final DocumentFacade documentFacade;
    private final DocumentRepository documentRepository;
    private final RevisionComponent revisionComponent;
    private final UserFacade userFacade;
    private final UserFilter userFilter;

    public UpdateInfo(DocumentFacade documentFacade, DocumentRepository documentRepository, RevisionComponent revisionComponent, UserFacade userFacade, UserFilter userFilter) {
        this.documentFacade = documentFacade;
        this.documentRepository = documentRepository;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
        this.userFilter = userFilter;
    }

    public Mono<Void> execute(UpdateInfoRequest request) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(request.documentId()))
                .flatMap(tuple -> checkPermissionAndUpdateDocument(tuple, request))
                .onErrorMap(this::mapException);
    }

    private Mono<Void> checkPermissionAndUpdateDocument(Tuple2<User, DefaultDocument> tuple, UpdateInfoRequest request) {
        userFilter.userPermissionAndDocumentVersionCheck(tuple.getT2(), tuple.getT1().getEmail(), request.version());

        DefaultDocument document = tuple.getT2();
        User user = tuple.getT1();

        setDocument(document, user, request.subTitle(), request.details());

        return documentRepository.save(document)
                .then(createRevision(document));
    }

    private void setDocument(DefaultDocument document, User user, String subTitle, List<Detail> details) {
        document.getEditor().setUpdatedUser(UserDetailResponse.create(user));
        document.getInfo().update(subTitle, details);
        document.increaseVersion();
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return revisionComponent.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

    private Throwable mapException(Throwable e) {
        return e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
