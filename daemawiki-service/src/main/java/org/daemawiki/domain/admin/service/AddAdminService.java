package org.daemawiki.domain.admin.service;

import org.daemawiki.domain.admin.application.CreateAdminAccountPort;
import org.daemawiki.domain.admin.model.Admin;
import org.daemawiki.domain.admin.usecase.AddAdminUsecase;
import org.daemawiki.domain.user.application.FindUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AddAdminService implements AddAdminUsecase {
    private final CreateAdminAccountPort createAdminAccountPort;
    private final FindUserPort findUserPort;

    public AddAdminService(CreateAdminAccountPort createAdminAccountPort, FindUserPort findUserPort) {
        this.createAdminAccountPort = createAdminAccountPort;
        this.findUserPort = findUserPort;
    }

    @Value("${daemawiki.admin.email}")
    private String adminEmail;

    @Override
    public Mono<Void> add(String email) {
        return validate()
                .then(createAdminAccount(email));
    }

    private Mono<Void> createAdminAccount(String email) {
        return createAdminAccountPort.create(
                Admin.builder()
                        .email(email)
                        .build()
        ).then();
    }

    private Mono<User> validate() {
        return findUserPort.currentUser()
                .filter(user -> user.getEmail().equals(adminEmail))
                .switchIfEmpty(Mono.error(NoPermissionUserException.EXCEPTION));
    }

}
