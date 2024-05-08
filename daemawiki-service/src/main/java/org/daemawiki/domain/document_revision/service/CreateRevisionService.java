package org.daemawiki.domain.document_revision.service;

import org.daemawiki.domain.document_editor.model.Editor;
import org.daemawiki.domain.document_revision.port.SaveRevisionPort;
import org.daemawiki.domain.document_revision.dto.request.SaveRevisionHistoryDto;
import org.daemawiki.domain.document_revision.model.RevisionHistory;
import org.daemawiki.domain.document_revision.usecase.CreateRevisionUsecase;
import org.daemawiki.domain.user.port.FindUserPort;
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
    public Mono<Void> saveHistory(SaveRevisionHistoryDto request) {
        return findUserPort.currentUser()
                .map(user -> createRevision(request, user))
                .flatMap(saveRevisionPort::save)
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION)
                .then();
    }

    private RevisionHistory createRevision(SaveRevisionHistoryDto request, User user) {
        return RevisionHistory.builder()
                .type(request.type())
                .documentId(request.documentId())
                .version(request.version())
                .title(request.title())
                .editor(Editor.of(user.getName(), user.getId()))
                .createdDateTime(LocalDateTime.now())
                .data(request.data())
                .build();
    }

}
