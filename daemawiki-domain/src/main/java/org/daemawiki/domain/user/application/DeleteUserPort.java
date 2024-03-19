package org.daemawiki.domain.user.application;

import org.daemawiki.domain.user.model.User;
import reactor.core.publisher.Mono;

public interface DeleteUserPort {
    Mono<Void> delete(User user);

}
