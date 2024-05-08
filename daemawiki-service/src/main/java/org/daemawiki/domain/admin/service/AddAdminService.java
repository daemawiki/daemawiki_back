package org.daemawiki.domain.admin.service;

import org.daemawiki.domain.admin.application.SaveAdminAccountPort;
import org.daemawiki.domain.admin.component.ValidateAdminComponent;
import org.daemawiki.domain.admin.model.Admin;
import org.daemawiki.domain.admin.usecase.AddAdminUsecase;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AddAdminService implements AddAdminUsecase {
    private final SaveAdminAccountPort saveAdminAccountPort;
    private final ValidateAdminComponent validateAdminComponent;

    public AddAdminService(SaveAdminAccountPort saveAdminAccountPort, ValidateAdminComponent validateAdminComponent) {
        this.saveAdminAccountPort = saveAdminAccountPort;
        this.validateAdminComponent = validateAdminComponent;
    }

    @Override
    public Mono<Void> add(String email) {
        return validateAdminComponent.validateSuperAdmin()
                .then(createAdminAccount(email));
    }

    private Mono<Void> createAdminAccount(String email) {
        return saveAdminAccountPort.save(
                Admin.create(email)
        ).then();
    }

}
