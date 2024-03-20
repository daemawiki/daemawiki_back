package org.daemawiki.domain.user.usecase.service;

import org.daemawiki.domain.document.application.GetDocumentPort;
import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.revision.usecase.CreateRevisionUsecase;
import org.daemawiki.domain.user.application.GetUserPort;
import org.daemawiki.domain.user.application.SaveUserPort;
import org.daemawiki.domain.user.dto.request.EditUserRequest;
import org.daemawiki.domain.user.dto.request.UpdateClubRequest;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.usecase.UpdateUserUsecase;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class UpdateUserService implements UpdateUserUsecase {
    private final GetUserPort getUserPort;
    private final GetDocumentPort getDocumentPort;
    private final SaveDocumentPort saveDocumentPort;
    private final SaveUserPort saveUserPort;
    private final CreateRevisionUsecase createRevisionUsecase;

    public UpdateUserService(GetUserPort getUserPort, GetDocumentPort getDocumentPort, SaveDocumentPort saveDocumentPort, SaveUserPort saveUserPort, CreateRevisionUsecase createRevisionUsecase) {
        this.getUserPort = getUserPort;
        this.getDocumentPort = getDocumentPort;
        this.saveDocumentPort = saveDocumentPort;
        this.saveUserPort = saveUserPort;
        this.createRevisionUsecase = createRevisionUsecase;
    }

    @Override
    public Mono<Void> updateUserClub(UpdateClubRequest request) {
        return getUserPort.currentUser()
                .doOnNext(user -> user.getDetail().setClub(request.club()))
                .flatMap(saveUserPort::save)
                .flatMap(user -> getDocumentPort.getDocumentById(user.getDocumentId())
                        .doOnNext(document -> setDocumentForUpdateUserClub(document, request.club()))
                        .flatMap(document -> saveDocumentPort.save(document)
                                .then(createRevision(document))))
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

    private void setDocumentForUpdateUserClub(DefaultDocument document, String requestClub) {
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

    @Override
    public Mono<Void> updateUser(EditUserRequest request) {
        return getUserPort.currentUser()
                .doOnNext(user -> user.update(request.name(), request.detail()))
                .flatMap(saveUserPort::save)
                .flatMap(user -> getDocumentPort.getDocumentById(user.getDocumentId())
                        .doOnNext(document -> setDocumentForUpdateUser(document, user))
                        .flatMap(document -> saveDocumentPort.save(document)
                                    .then(createRevision(document))));
    }

    private void setDocumentForUpdateUser(DefaultDocument document, User user) {
        List<List<String>> newGroups = createNewGroups(user);

        document.updateByUserEdit(user.getName(), newGroups);
        document.increaseVersion();
    }

    private List<List<String>> createNewGroups(User user) {
        List<List<String>> newGroups = new ArrayList<>();
        newGroups.add(List.of("학생", user.getDetail().getGen() + "기", user.getName()));
        newGroups.add(List.of("전공", user.getDetail().getMajor().getMajor()));

        if (!user.getDetail().getClub().isBlank()) {
            newGroups.add(List.of("동아리", user.getDetail().getClub()));
        }

        return newGroups;
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return createRevisionUsecase.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

}
