package com.example.daemawiki.domain.user.service;

import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
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

    public UpdateClub(UserFacade userFacade, DocumentFacade documentFacade, UserRepository userRepository, DocumentRepository documentRepository, Scheduler scheduler) {
        this.userFacade = userFacade;
        this.documentFacade = documentFacade;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.scheduler = scheduler;
    }

    public Mono<Void> execute(UpdateClubRequest request) {
        return userFacade.currentUser()
                .doOnNext(user -> user.getDetail().setClub(request.club()))
                .flatMap(userRepository::save)
                .flatMap(user -> documentFacade.findDocumentById(user.getDocumentId())
                        .doOnNext(document -> document.getGroups().add(Arrays.asList("동아리", request.club())))
                        .flatMap(documentRepository::save))
                .subscribeOn(scheduler)
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION)
                .then();
    }

}
