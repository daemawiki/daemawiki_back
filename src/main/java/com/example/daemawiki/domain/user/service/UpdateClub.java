package com.example.daemawiki.domain.user.service;

import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.dto.request.UpdateClubRequest;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class UpdateClub {
    private final UserFacade userFacade;
    private final DocumentFacade documentFacade;
    private final UserRepository userRepository;
    private final RevisionComponent revisionComponent;

    public UpdateClub(UserFacade userFacade, DocumentFacade documentFacade, UserRepository userRepository, RevisionComponent revisionComponent) {
        this.userFacade = userFacade;
        this.documentFacade = documentFacade;
        this.userRepository = userRepository;
        this.revisionComponent = revisionComponent;
    }

    public Mono<Void> execute(UpdateClubRequest request) {
        return userFacade.currentUser()
                .doOnNext(user -> user.getDetail().setClub(request.club()))
                .flatMap(userRepository::save)
                .flatMap(user -> documentFacade.findDocumentById(user.getDocumentId())
                        .doOnNext(document -> setDocument(document, request.club()))
                        .flatMap(document -> documentFacade.saveDocument(document)
                                .then(createRevision(document))))
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

    private void setDocument(DefaultDocument document, String requestClub) {
        if (!Objects.equals(requestClub, "*")) {
            document.getGroups().add(Arrays.asList("동아리", requestClub));
        } else {
            List<List<String>> newGroups = document.getGroups().stream()
                    .filter(g -> !g.contains("동아리"))
                    .toList();

            document.setGroups(newGroups);
        }
        document.increaseVersion();
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return revisionComponent.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

}
