package org.daemawiki.domain.mail.application.mail;

import reactor.core.publisher.Mono;

public interface GetAuthMailPort {
    Mono<Boolean> findByMail(String mail);

}
