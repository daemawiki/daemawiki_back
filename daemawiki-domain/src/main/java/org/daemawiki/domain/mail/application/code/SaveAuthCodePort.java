package org.daemawiki.domain.mail.application.code;

import org.daemawiki.domain.mail.model.AuthCode;
import reactor.core.publisher.Mono;

public interface SaveAuthCodePort {
    Mono<Void> save(AuthCode authCode);

}
