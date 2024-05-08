package org.daemawiki.domain.document_info.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.document.port.FindDocumentPort;
import org.daemawiki.domain.document.port.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document_info.usecase.UploadDocumentImageUsecase;
import org.daemawiki.domain.document_revision.component.CreateRevisionComponent;
import org.daemawiki.domain.document_revision.model.type.RevisionType;
import org.daemawiki.domain.file.model.File;
import org.daemawiki.domain.file.model.type.FileType;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.infra.s3.service.S3UploadObject;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class UploadDocumentImageService implements UploadDocumentImageUsecase {
    private final S3UploadObject s3UploadObject;
    private final FindUserPort findUserPort;
    private final FindDocumentPort findDocumentPort;
    private final SaveDocumentPort saveDocumentPort;
    private final UserFilter userFilter;
    private final CreateRevisionComponent createRevisionComponent;

    public UploadDocumentImageService(S3UploadObject s3UploadObject, FindUserPort findUserPort, FindDocumentPort findDocumentPort, SaveDocumentPort saveDocumentPort, UserFilter userFilter, CreateRevisionComponent createRevisionComponent) {
        this.s3UploadObject = s3UploadObject;
        this.findUserPort = findUserPort;
        this.findDocumentPort = findDocumentPort;
        this.saveDocumentPort = saveDocumentPort;
        this.userFilter = userFilter;
        this.createRevisionComponent = createRevisionComponent;
    }

    @Override
    public Mono<Void> upload(FilePart filePart, String documentId) {
        return findUserPort.currentUser()
                .zipWith(findDocumentPort.findById(documentId))
                .map(this::checkPermission)
                .zipWith(s3UploadObject.uploadObject(filePart, FileType.DOCUMENT.toString()))
                .flatMap(this::updateDocument);
    }

    private Mono<Void> updateDocument(Tuple2<DefaultDocument, File> tuple) {
        DefaultDocument document = tuple.getT1();
        File file = tuple.getT2();

        document.getInfo().setDocumentImage(file);

        return saveDocumentPort.save(document)
                .then(createRevisionComponent.create(document, RevisionType.UPDATE));
    }

    private DefaultDocument checkPermission(Tuple2<User, DefaultDocument> tuple) {
        userFilter.userPermissionCheck(tuple.getT2(), tuple.getT1());
        return tuple.getT2();
    }

}
