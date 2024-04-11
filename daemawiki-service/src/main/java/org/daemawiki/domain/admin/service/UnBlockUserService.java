package org.daemawiki.domain.admin.service;

import org.daemawiki.domain.admin.application.FindAdminAccountPort;
import org.daemawiki.domain.admin.model.Admin;
import org.daemawiki.domain.admin.usecase.UnBlockUserUsecase;
import org.daemawiki.domain.user.application.FindUserPort;
import org.daemawiki.domain.user.application.SaveUserPort;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UnBlockUserService implements UnBlockUserUsecase {
    private final FindAdminAccountPort findAdminAccountPort;
    private final FindUserPort findUserPort;
    private final SaveUserPort saveUserPort;
    
    public UnBlockUserService(FindAdminAccountPort findAdminAccountPort, FindUserPort findUserPort, SaveUserPort saveUserPort) {
        this.findAdminAccountPort = findAdminAccountPort;
        this.findUserPort = findUserPort;
        this.saveUserPort = saveUserPort;
    }

    @Override
    public Mono<Void> unblock(String userId) {
        return validate()
                .flatMap(admin -> findUserPort.findById(userId)
                        .doOnNext(user -> user.setIsBlocked(false))
                        .flatMap(saveUserPort::save))
                .then();
    }

    private Mono<Admin> validate() {
        return findUserPort.currentUser()
                .flatMap(user -> findAdminAccountPort.findByUserId(user.getId()))
                .switchIfEmpty(Mono.error(NoPermissionUserException.EXCEPTION));
    }
    
}
