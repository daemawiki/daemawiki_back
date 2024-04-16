package org.daemawiki.domain.admin.application;

import org.daemawiki.domain.admin.model.Admin;
import reactor.core.publisher.Mono;

public interface SaveAdminAccountPort {
    Mono<Admin> save(Admin admin);

}
