package org.daemawiki.domain.info.usecase.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.document.application.GetDocumentPort;
import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.info.dto.UpdateInfoRequest;
import org.daemawiki.domain.info.model.Detail;
import org.daemawiki.domain.info.usecase.UpdateDocumentInfoUsecase;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.revision.usecase.CreateRevisionUsecase;
import org.daemawiki.domain.user.application.GetUserPort;
import org.daemawiki.domain.user.dto.response.UserDetailResponse;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h400.VersionMismatchException;
import org.daemawiki.exception.h403.NoEditPermissionUserException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

@Service
public class UpdateDocumentInfoService implements UpdateDocumentInfoUsecase {
    private final SaveDocumentPort saveDocumentPort;
    private final GetDocumentPort getDocumentPort;
    private final CreateRevisionUsecase createRevisionUsecase;
    private final GetUserPort getUserPort;
    private final UserFilter userFilter;

    public UpdateDocumentInfoService(SaveDocumentPort saveDocumentPort, GetDocumentPort getDocumentPort, CreateRevisionUsecase createRevisionUsecase, GetUserPort getUserPort, UserFilter userFilter) {
        this.saveDocumentPort = saveDocumentPort;
        this.getDocumentPort = getDocumentPort;
        this.createRevisionUsecase = createRevisionUsecase;
        this.getUserPort = getUserPort;
        this.userFilter = userFilter;
    }

    @Override
    public Mono<Void> update(String documentId, UpdateInfoRequest request) {
        return getUserPort.currentUser()
                .zipWith(getDocumentPort.getDocumentById(documentId))
                .flatMap(tuple -> checkPermissionAndUpdateDocument(tuple, request))
                .onErrorMap(this::mapException);
    }

    private Mono<Void> checkPermissionAndUpdateDocument(Tuple2<User, DefaultDocument> tuple, UpdateInfoRequest request) {
        userFilter.userPermissionAndDocumentVersionCheck(tuple.getT2(), tuple.getT1().getEmail(), request.version());

        DefaultDocument document = tuple.getT2();
        User user = tuple.getT1();

        setDocument(document, user, request.subTitle(), request.details());

        return saveDocumentPort.save(document)
                .then(createRevision(document));
    }

    private void setDocument(DefaultDocument document, User user, String subTitle, List<Detail> details) {
        document.getEditor().setUpdatedUser(UserDetailResponse.create(user));
        document.getInfo().update(subTitle, details);
        document.increaseVersion();
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return createRevisionUsecase.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

    private Throwable mapException(Throwable e) {
        return e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
