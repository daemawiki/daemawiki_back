package org.daemawiki.domain.mail.port.mail;

import org.daemawiki.domain.mail.model.AuthMail;
import reactor.core.publisher.Mono;

public interface SaveAuthMailPort {
    Mono<Void> save(AuthMail authMail);

}
