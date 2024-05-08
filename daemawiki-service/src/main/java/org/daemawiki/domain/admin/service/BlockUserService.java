package org.daemawiki.domain.admin.service;

import org.daemawiki.domain.admin.component.ValidateAdminComponent;
import org.daemawiki.domain.admin.usecase.BlockUserUsecase;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.port.SaveUserPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BlockUserService implements BlockUserUsecase {
    private final SaveUserPort saveUserPort;
    private final FindUserPort findUserPort;
    private final ValidateAdminComponent validateAdminComponent;

    public BlockUserService(SaveUserPort saveUserPort, FindUserPort findUserPort, ValidateAdminComponent validateAdminComponent) {
        this.saveUserPort = saveUserPort;
        this.findUserPort = findUserPort;
        this.validateAdminComponent = validateAdminComponent;
    }

    @Override
    public Mono<Void> block(String userId) {
        return validateAdminComponent.validateAdmin()
                .flatMap(admin -> findUserPort.findById(userId)
                        .doOnNext(user -> user.setIsBlocked(true))
                        .flatMap(saveUserPort::save))
                .then();
    }

}
