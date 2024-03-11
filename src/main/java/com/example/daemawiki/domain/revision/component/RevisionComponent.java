package com.example.daemawiki.domain.revision.component;

import com.example.daemawiki.domain.editor.model.Editor;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.RevisionHistory;
import com.example.daemawiki.domain.revision.repository.RevisionHistoryRepository;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class RevisionComponent {
    private final RevisionHistoryRepository revisionHistoryRepository;
    private final UserFacade userFacade;

    public RevisionComponent(RevisionHistoryRepository revisionHistoryRepository, UserFacade userFacade) {
        this.revisionHistoryRepository = revisionHistoryRepository;
        this.userFacade = userFacade;
    }

    public Mono<Void> saveHistory(SaveRevisionHistoryRequest request) {
        return userFacade.currentUser()
                .flatMap(user -> createRevision(request, user))
                .flatMap(revisionHistoryRepository::save)
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION)
                .then();
    }

    private Mono<RevisionHistory> createRevision(SaveRevisionHistoryRequest request, User user) {
        return Mono.just(RevisionHistory.builder()
                .type(request.type())
                .documentId(request.documentId())
                .title(request.title())
                .editor(Editor.create(user.getName(), user.getId()))
                .createdDateTime(LocalDateTime.now())
                .build());
    }

}
