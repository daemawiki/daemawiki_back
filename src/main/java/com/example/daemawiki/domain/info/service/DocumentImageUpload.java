package com.example.daemawiki.domain.info.service;

import com.example.daemawiki.domain.common.UserFilter;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.file.model.File;
import com.example.daemawiki.domain.file.model.type.FileType;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.infra.s3.service.S3UploadObject;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DocumentImageUpload {
    private final S3UploadObject s3UploadObject;
    private final UserFacade userFacade;
    private final DocumentFacade documentFacade;
    private final UserFilter userFilter;
    private final RevisionComponent revisionComponent;

    public DocumentImageUpload(S3UploadObject s3UploadObject, UserFacade userFacade, DocumentFacade documentFacade, UserFilter userFilter, RevisionComponent revisionComponent) {
        this.s3UploadObject = s3UploadObject;
        this.userFacade = userFacade;
        this.documentFacade = documentFacade;
        this.userFilter = userFilter;
        this.revisionComponent = revisionComponent;
    }

    public Mono<Void> execute(FilePart filePart, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId))
                .map(tuple -> {
                    userFilter.userPermissionCheck(tuple.getT2(), tuple.getT1().getId());
                    return tuple.getT2();
                })
                .zipWith(s3UploadObject.uploadObject(filePart, FileType.DOCUMENT.toString()))
                .flatMap(tuple -> {
                    DefaultDocument document = tuple.getT1();
                    File file = tuple.getT2();

                    document.getInfo().setDocumentImage(file);

                    return documentFacade.saveDocument(document)
                            .then(createRevision(document));
                });
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return revisionComponent.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

}
