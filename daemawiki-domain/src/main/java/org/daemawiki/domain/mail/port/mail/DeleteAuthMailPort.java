package org.daemawiki.domain.mail.port.mail;

import reactor.core.publisher.Mono;

public interface DeleteAuthMailPort {
    Mono<Void> delete(String mail);

}
