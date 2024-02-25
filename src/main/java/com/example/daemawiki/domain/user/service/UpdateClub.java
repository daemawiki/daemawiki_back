package com.example.daemawiki.domain.user.service;

import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.user.dto.UpdateClubRequest;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Service
public class UpdateClub {
    private final UserFacade userFacade;
    private final DocumentFacade documentFacade;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    public UpdateClub(UserFacade userFacade, DocumentFacade documentFacade, UserRepository userRepository, DocumentRepository documentRepository) {
        this.userFacade = userFacade;
        this.documentFacade = documentFacade;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
    }

    public Mono<Void> execute(UpdateClubRequest request) {
        Mono<User> userMono = userFacade.currentUser()
                .doOnNext(user -> user.getDetail().setClub(request.club()))
                .flatMap(userRepository::save);

        Mono<DefaultDocument> documentMono = userMono
                .flatMap(user -> documentFacade.findDocumentById(user.getDocumentId()))
                .doOnNext(document -> document.getGroups().add(Arrays.asList("동아리", request.club())))
                .flatMap(documentRepository::save);

        return Mono.when(userMono, documentMono).then();
    }
    
}
