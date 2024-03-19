package org.daemawiki.domain.mail.usecase;

import org.daemawiki.domain.mail.dto.AuthCodeVerifyRequest;
import org.daemawiki.domain.mail.dto.AuthCodeVerifyResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface UserMailVerifyUsecase {
    Mono<ResponseEntity<AuthCodeVerifyResponse>> verify(AuthCodeVerifyRequest request);

}
