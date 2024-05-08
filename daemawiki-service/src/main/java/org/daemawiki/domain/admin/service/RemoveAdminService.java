package org.daemawiki.domain.admin.service;

import org.daemawiki.domain.admin.application.DeleteAdminAccountPort;
import org.daemawiki.domain.admin.application.FindAdminAccountPort;
import org.daemawiki.domain.admin.component.ValidateAdminComponent;
import org.daemawiki.domain.admin.model.Admin;
import org.daemawiki.domain.admin.usecase.RemoveAdminUsecase;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.port.SaveUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h404.UserNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RemoveAdminService implements RemoveAdminUsecase {
    private final DeleteAdminAccountPort deleteAdminAccountPort;
    private final FindAdminAccountPort findAdminAccountPort;
    private final FindUserPort findUserPort;
    private final SaveUserPort saveUserPort;
    private final ValidateAdminComponent validateAdminComponent;

    public RemoveAdminService(DeleteAdminAccountPort deleteAdminAccountPort, FindAdminAccountPort findAdminAccountPort, FindUserPort findUserPort, SaveUserPort saveUserPort, ValidateAdminComponent validateAdminComponent) {
        this.deleteAdminAccountPort = deleteAdminAccountPort;
        this.findAdminAccountPort = findAdminAccountPort;
        this.findUserPort = findUserPort;
        this.saveUserPort = saveUserPort;
        this.validateAdminComponent = validateAdminComponent;
    }

    @Override
    public Mono<Void> remove(String email) {
        return validateAdminComponent.validateSuperAdmin()
                .flatMap(user -> findAdminAccountPort.findByEmail(email))
                .switchIfEmpty(Mono.error(UserNotFoundException.EXCEPTION))
                .flatMap(this::deleteAdmin);
    }

    private Mono<Void> deleteAdmin(Admin admin) {
        if (!admin.getUserId().equals("not yet")) {
            return setRole(admin);
        }
        return deleteByEmail(admin.getEmail());
    }

    private Mono<Void> setRole(Admin admin) {
        return findUserPort.findById(admin.getUserId())
                .doOnNext(u -> u.setRole(User.Role.USER))
                .flatMap(saveUserPort::save)
                .then(deleteByEmail(admin.getEmail()));
    }

    private Mono<Void> deleteByEmail(String email) {
        return deleteAdminAccountPort.deleteByEmail(email);
    }

}
