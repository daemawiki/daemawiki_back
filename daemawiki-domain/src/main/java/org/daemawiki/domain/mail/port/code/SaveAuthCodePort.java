package org.daemawiki.domain.mail.port.code;

import org.daemawiki.domain.mail.model.AuthCode;
import reactor.core.publisher.Mono;

public interface SaveAuthCodePort {
    Mono<Void> save(AuthCode authCode);

}
