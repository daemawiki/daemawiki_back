package org.daemawiki.domain.mail.application.mail;

import reactor.core.publisher.Mono;

public interface DeleteAuthMailPort {
    Mono<Void> delete(String mail);

}
