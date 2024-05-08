package org.daemawiki.domain.mail.port.mail;

import reactor.core.publisher.Mono;

public interface FindAuthMailPort {
    Mono<Boolean> findByMail(String mail);

}
