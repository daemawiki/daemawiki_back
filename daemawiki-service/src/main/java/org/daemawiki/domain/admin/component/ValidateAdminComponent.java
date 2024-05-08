package org.daemawiki.domain.admin.component;

import org.daemawiki.domain.admin.model.Admin;
import org.daemawiki.domain.user.model.User;
import reactor.core.publisher.Mono;

public interface ValidateAdminComponent {
    Mono<User> validateSuperAdmin();
    Mono<Admin> validateAdmin();

}
