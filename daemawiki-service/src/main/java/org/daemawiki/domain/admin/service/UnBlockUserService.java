package org.daemawiki.domain.admin.service;

import org.daemawiki.domain.admin.component.ValidateAdminComponent;
import org.daemawiki.domain.admin.usecase.UnBlockUserUsecase;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.port.SaveUserPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UnBlockUserService implements UnBlockUserUsecase {
    private final FindUserPort findUserPort;
    private final SaveUserPort saveUserPort;
    private final ValidateAdminComponent validateAdminComponent;
    
    public UnBlockUserService(FindUserPort findUserPort, SaveUserPort saveUserPort, ValidateAdminComponent validateAdminComponent) {
        this.findUserPort = findUserPort;
        this.saveUserPort = saveUserPort;
        this.validateAdminComponent = validateAdminComponent;
    }

    @Override
    public Mono<Void> unblock(String userId) {
        return validateAdminComponent.validateAdmin()
                .flatMap(admin -> findUserPort.findById(userId)
                        .doOnNext(user -> user.setIsBlocked(false))
                        .flatMap(saveUserPort::save))
                .then();
    }

}
