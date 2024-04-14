package org.daemawiki.domain.admin.service;

import org.daemawiki.domain.admin.application.DeleteAdminAccountPort;
import org.daemawiki.domain.admin.application.FindAdminAccountPort;
import org.daemawiki.domain.admin.model.Admin;
import org.daemawiki.domain.admin.usecase.RemoveAdminUsecase;
import org.daemawiki.domain.user.application.FindUserPort;
import org.daemawiki.domain.user.application.SaveUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.daemawiki.exception.h404.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RemoveAdminService implements RemoveAdminUsecase {
    private final DeleteAdminAccountPort deleteAdminAccountPort;
    private final FindAdminAccountPort findAdminAccountPort;
    private final FindUserPort findUserPort;
    private final SaveUserPort saveUserPort;

    public RemoveAdminService(DeleteAdminAccountPort deleteAdminAccountPort, FindAdminAccountPort findAdminAccountPort, FindUserPort findUserPort, SaveUserPort saveUserPort) {
        this.deleteAdminAccountPort = deleteAdminAccountPort;
        this.findAdminAccountPort = findAdminAccountPort;
        this.findUserPort = findUserPort;
        this.saveUserPort = saveUserPort;
    }

    @Value("${daemawiki.admin.email}")
    private String adminEmail;

    @Override
    public Mono<Void> remove(String email) {
        return validate()
                .flatMap(user -> findAdminAccountPort.findByEmail(email))
                        .switchIfEmpty(Mono.error(UserNotFoundException.EXCEPTION))
                        .flatMap(delAdmin -> {
                            if (!delAdmin.getUserId().equals("not yet")) {
                                return setRole(delAdmin);
                            }
                            return deleteAdmin(email);
                        });
    }

    private Mono<Void> setRole(Admin user) {
        return findUserPort.findById(user.getUserId())
                .doOnNext(u -> u.setRole(User.Role.USER))
                .flatMap(saveUserPort::save)
                .then(deleteAdmin(user.getEmail()));
    }

    private Mono<Void> deleteAdmin(String email) {
        return deleteAdminAccountPort.deleteByEmail(email);
    }

    private Mono<User> validate() {
        return findUserPort.currentUser()
                .filter(user -> user.getEmail().equals(adminEmail))
                .switchIfEmpty(Mono.error(NoPermissionUserException.EXCEPTION));
    }

}
