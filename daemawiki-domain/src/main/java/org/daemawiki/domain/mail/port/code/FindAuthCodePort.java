package org.daemawiki.domain.mail.port.code;

import org.daemawiki.domain.mail.model.AuthCode;
import reactor.core.publisher.Mono;

public interface FindAuthCodePort {
    Mono<AuthCode> findByMail(String mail);

}
