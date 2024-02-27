package com.example.daemawiki.domain.user.service;

import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.dto.request.UpdateClubRequest;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Arrays;

@Service
public class UpdateClub {
    private final UserFacade userFacade;
    private final DocumentFacade documentFacade;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final Scheduler scheduler;
    private final RevisionComponent revisionComponent;

    public UpdateClub(UserFacade userFacade, DocumentFacade documentFacade, UserRepository userRepository, DocumentRepository documentRepository, Scheduler scheduler, RevisionComponent revisionComponent) {
        this.userFacade = userFacade;
        this.documentFacade = documentFacade;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.scheduler = scheduler;
        this.revisionComponent = revisionComponent;
    }

    public Mono<Void> execute(UpdateClubRequest request) {
        return userFacade.currentUser()
                .doOnNext(user -> user.getDetail().setClub(request.club()))
                .flatMap(userRepository::save)
                .flatMap(user -> documentFacade.findDocumentById(user.getDocumentId())
                        .doOnNext(document -> document.getGroups().add(Arrays.asList("동아리", request.club())))
                        .flatMap(documentRepository::save))
                .subscribeOn(scheduler)
                .flatMap(document -> revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                        .type(RevisionType.UPDATE)
                        .documentId(document.getId())
                        .title(document.getTitle())
                        .build()))
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

}
