package org.daemawiki.domain.admin.service;

import org.daemawiki.domain.admin.application.FindAdminAccountPort;
import org.daemawiki.domain.admin.model.Admin;
import org.daemawiki.domain.admin.usecase.BlockUserUsecase;
import org.daemawiki.domain.user.application.FindUserPort;
import org.daemawiki.domain.user.application.SaveUserPort;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BlockUserService implements BlockUserUsecase {
    private final FindAdminAccountPort findAdminAccountPort;
    private final SaveUserPort saveUserPort;
    private final FindUserPort findUserPort;

    public BlockUserService(FindAdminAccountPort findAdminAccountPort, SaveUserPort saveUserPort, FindUserPort findUserPort) {
        this.findAdminAccountPort = findAdminAccountPort;
        this.saveUserPort = saveUserPort;
        this.findUserPort = findUserPort;
    }

    @Override
    public Mono<Void> block(String userId) {
        return validate()
                .flatMap(admin -> findUserPort.findById(userId)
                        .doOnNext(user -> user.setIsBlocked(true))
                        .flatMap(saveUserPort::save))
                .then();
    }

    private Mono<Admin> validate() {
        return findUserPort.currentUser()
                .flatMap(user -> findAdminAccountPort.findByUserId(user.getId()))
                .switchIfEmpty(Mono.error(NoPermissionUserException.EXCEPTION));
    }

}
