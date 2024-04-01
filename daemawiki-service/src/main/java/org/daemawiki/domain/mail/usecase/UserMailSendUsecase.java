package org.daemawiki.domain.mail.usecase;

import org.daemawiki.domain.mail.dto.AuthCodeRequest;
import reactor.core.publisher.Mono;

public interface UserMailSendUsecase {
    Mono<Void> send(AuthCodeRequest request);

}
