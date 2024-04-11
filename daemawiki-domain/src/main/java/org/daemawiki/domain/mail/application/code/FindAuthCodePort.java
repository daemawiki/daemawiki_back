package org.daemawiki.domain.mail.application.code;

import org.daemawiki.domain.mail.model.AuthCode;
import reactor.core.publisher.Mono;

public interface FindAuthCodePort {
    Mono<AuthCode> findByMail(String mail);

}
