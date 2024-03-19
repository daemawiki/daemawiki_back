package org.daemawiki.domain.user.application;

import org.daemawiki.domain.user.model.User;
import reactor.core.publisher.Mono;

public interface SaveUserPort {
    Mono<User> save(User user);

}
