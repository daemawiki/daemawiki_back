package org.daemawiki.domain.user.service;

import org.daemawiki.domain.document.port.FindDocumentPort;
import org.daemawiki.domain.document.port.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document_revision.component.CreateRevisionComponent;
import org.daemawiki.domain.document_revision.model.type.RevisionType;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.port.SaveUserPort;
import org.daemawiki.domain.user.dto.request.UpdateUserRequest;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.usecase.UpdateUserUsecase;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateUserService implements UpdateUserUsecase {
    private final FindUserPort findUserPort;
    private final FindDocumentPort findDocumentPort;
    private final SaveDocumentPort saveDocumentPort;
    private final SaveUserPort saveUserPort;
    private final CreateRevisionComponent createRevisionComponent;

    public UpdateUserService(FindUserPort findUserPort, FindDocumentPort findDocumentPort, SaveDocumentPort saveDocumentPort, SaveUserPort saveUserPort, CreateRevisionComponent createRevisionComponent) {
        this.findUserPort = findUserPort;
        this.findDocumentPort = findDocumentPort;
        this.saveDocumentPort = saveDocumentPort;
        this.saveUserPort = saveUserPort;
        this.createRevisionComponent = createRevisionComponent;
    }

    @Override
    public Mono<Void> updateUser(UpdateUserRequest request) {
        return findUserPort.currentUser()
                .doOnNext(user -> user.update(request.name(), request.detail()))
                .flatMap(saveUserPort::save)
                .flatMap(this::updateUserDocument);
    }

    private Mono<Void> updateUserDocument(User user) {
        return findDocumentPort.findById(user.getDocumentId())
                .doOnNext(document -> setDocumentForUpdateUser(document, user))
                .flatMap(this::saveDocumentAndCreateRevision);
    }

    private Mono<Void> saveDocumentAndCreateRevision(DefaultDocument document) {
        return saveDocumentPort.save(document)
                .then(createRevisionComponent.create(document, RevisionType.UPDATE));
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

}
