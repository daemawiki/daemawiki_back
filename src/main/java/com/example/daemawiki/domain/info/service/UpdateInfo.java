package com.example.daemawiki.domain.info.service;

import com.example.daemawiki.domain.common.UserFilter;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.info.dto.UpdateInfoRequest;
import com.example.daemawiki.domain.info.model.Info;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.dto.response.UserDetailResponse;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h400.VersionMismatchException;
import com.example.daemawiki.global.exception.h403.NoEditPermissionUserException;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
                .map(tuple -> {
                    userFilter.userPermissionAndDocumentVersionCheck(tuple.getT2(), tuple.getT1().getEmail(), request.version());
                    return tuple;
                })
                .flatMap(tuple -> {
                    DefaultDocument document = tuple.getT2();
                    User user = tuple.getT1();

                    setDocument(document, user, request.infoList());

                    return documentRepository.save(document)
                            .then(createRevision(document));
                })
                .onErrorMap(this::mapException);
    }

    private void setDocument(DefaultDocument document, User user, List<Info> infoList) {
        document.getEditor().setUpdatedUser(UserDetailResponse.create(user));

        document.setInfo(infoList);
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
