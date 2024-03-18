package org.daemawiki.domain.revision.component;

import org.daemawiki.domain.editor.model.Editor;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.RevisionHistory;
import org.daemawiki.domain.revision.repository.RevisionHistoryRepository;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.service.facade.UserFacade;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class CreateRevisionHistory implements RevisionComponent {
    private final RevisionHistoryRepository revisionHistoryRepository;
    private final UserFacade userFacade;

    public CreateRevisionHistory(RevisionHistoryRepository revisionHistoryRepository, UserFacade userFacade) {
        this.revisionHistoryRepository = revisionHistoryRepository;
        this.userFacade = userFacade;
    }

    @Override
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
