package org.daemawiki.domain.revision.service;

import org.daemawiki.domain.editor.model.Editor;
import org.daemawiki.domain.revision.application.SaveRevisionPort;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.RevisionHistory;
import org.daemawiki.domain.revision.usecase.CreateRevisionUsecase;
import org.daemawiki.domain.user.application.FindUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class CreateRevisionService implements CreateRevisionUsecase {
    private final SaveRevisionPort saveRevisionPort;
    private final FindUserPort findUserPort;

    public CreateRevisionService(SaveRevisionPort saveRevisionPort, FindUserPort findUserPort) {
        this.saveRevisionPort = saveRevisionPort;
        this.findUserPort = findUserPort;
    }

    @Override
    public Mono<Void> saveHistory(SaveRevisionHistoryRequest request) {
        return findUserPort.currentUser()
                .flatMap(user -> createRevision(request, user))
                .flatMap(saveRevisionPort::save)
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
