package org.daemawiki.domain.admin.component;

import org.daemawiki.domain.admin.application.FindAdminAccountPort;
import org.daemawiki.domain.admin.model.Admin;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ValidateAdminComponentImpl implements ValidateAdminComponent {
    private final FindAdminAccountPort findAdminAccountPort;
    private final FindUserPort findUserPort;

    public ValidateAdminComponentImpl(FindAdminAccountPort findAdminAccountPort, FindUserPort findUserPort) {
        this.findAdminAccountPort = findAdminAccountPort;
        this.findUserPort = findUserPort;
    }

    @Value("${daemawiki.admin.email}")
    private String adminEmail;

    @Override
    public Mono<Admin> validateAdmin() {
        return findUserPort.currentUser()
                .flatMap(user -> findAdminAccountPort.findByUserId(user.getId()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(NoPermissionUserException.EXCEPTION)));
    }

    @Override
    public Mono<User> validateSuperAdmin() {
        return findUserPort.currentUser()
                .filter(user -> user.getEmail().equals(adminEmail))
                .switchIfEmpty(Mono.defer(() -> Mono.error(NoPermissionUserException.EXCEPTION)));
    }

}
