package com.example.daemawiki.domain.user.service;

import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.dto.request.EditUserRequest;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EditUser {
    private final UserFacade userFacade;
    private final DocumentFacade documentFacade;
    private final UserRepository userRepository;
    private final RevisionComponent revisionComponent;

    public EditUser(UserFacade userFacade, DocumentFacade documentFacade, UserRepository userRepository, RevisionComponent revisionComponent) {
        this.userFacade = userFacade;
        this.documentFacade = documentFacade;
        this.userRepository = userRepository;
        this.revisionComponent = revisionComponent;
    }

    public Mono<Void> execute(EditUserRequest request) {
        return userFacade.currentUser()
                .flatMap(user -> {
                    user.update(request.name(), request.detail());
                    return userRepository.save(user);
                })
                .flatMap(user -> documentFacade.findDocumentById(user.getDocumentId())
                        .flatMap(document -> {
                            setDocument(document, user);

                            return documentFacade.saveDocument(document)
                                    .then(createRevision(document));
                        }));
    }

    private void setDocument(DefaultDocument document, User user) {
        List<List<String>> newGroups = createNewGroups(user);

        document.updateByUserEdit(user.getName(), newGroups);
        document.increaseVersion();
    }

    private List<List<String>> createNewGroups(User user) {
        List<List<String>> newGroups = new ArrayList<>();
        newGroups.add(Arrays.asList("학생", user.getDetail().getGen() + "기", user.getName()));
        newGroups.add(Arrays.asList("전공", user.getDetail().getMajor().getMajor()));

        if (!user.getDetail().getClub().isBlank()) {
            newGroups.add(Arrays.asList("동아리", user.getDetail().getClub()));
        }

        return newGroups;
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                .type(RevisionType.UPDATE)
                .documentId(document.getId())
                .title(document.getTitle())
                .build());
    }

}
