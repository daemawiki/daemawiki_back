package org.daemawiki.domain.info.usecase.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.document.application.GetDocumentPort;
import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.file.model.File;
import org.daemawiki.domain.file.model.type.FileType;
import org.daemawiki.domain.info.usecase.UploadDocumentImageUsecase;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.revision.usecase.CreateRevisionUsecase;
import org.daemawiki.domain.user.application.GetUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.infra.s3.service.S3UploadObject;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class UploadDocumentImageService implements UploadDocumentImageUsecase {
    private final S3UploadObject s3UploadObject;
    private final GetUserPort getUserPort;
    private final GetDocumentPort getDocumentPort;
    private final SaveDocumentPort saveDocumentPort;
    private final UserFilter userFilter;
    private final CreateRevisionUsecase createRevisionUsecase;

    public UploadDocumentImageService(S3UploadObject s3UploadObject, GetUserPort getUserPort, GetDocumentPort getDocumentPort, SaveDocumentPort saveDocumentPort, UserFilter userFilter, CreateRevisionUsecase createRevisionUsecase) {
        this.s3UploadObject = s3UploadObject;
        this.getUserPort = getUserPort;
        this.getDocumentPort = getDocumentPort;
        this.saveDocumentPort = saveDocumentPort;
        this.userFilter = userFilter;
        this.createRevisionUsecase = createRevisionUsecase;
    }

    @Override
    public Mono<Void> upload(FilePart filePart, String documentId) {
        return getUserPort.currentUser()
                .zipWith(getDocumentPort.getDocumentById(documentId))
                .map(this::checkPermission)
                .zipWith(s3UploadObject.uploadObject(filePart, FileType.DOCUMENT.toString()))
                .flatMap(this::updateDocument);
    }

    private Mono<Void> updateDocument(Tuple2<DefaultDocument, File> tuple) {
        DefaultDocument document = tuple.getT1();
        File file = tuple.getT2();

        document.getInfo().setDocumentImage(file);

        return saveDocumentPort.save(document)
                .then(createRevision(document));
    }

    private DefaultDocument checkPermission(Tuple2<User, DefaultDocument> tuple) {
        userFilter.userPermissionCheck(tuple.getT2(), tuple.getT1().getId());
        return tuple.getT2();
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return createRevisionUsecase.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

}
