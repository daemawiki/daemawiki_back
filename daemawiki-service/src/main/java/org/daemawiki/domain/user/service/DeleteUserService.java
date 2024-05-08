package org.daemawiki.domain.user.service;

import org.daemawiki.domain.user.port.DeleteUserPort;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.usecase.DeleteUserUsecase;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DeleteUserService implements DeleteUserUsecase {
    private final FindUserPort findUserPort;
    private final DeleteUserPort deleteUserPort;

    public DeleteUserService(FindUserPort findUserPort, DeleteUserPort deleteUserPort) {
        this.findUserPort = findUserPort;
        this.deleteUserPort = deleteUserPort;
    }

    @Override
    public Mono<Void> deleteCurrentUser() {
        return findUserPort.currentUser()
                .flatMap(deleteUserPort::delete);
    }

}
