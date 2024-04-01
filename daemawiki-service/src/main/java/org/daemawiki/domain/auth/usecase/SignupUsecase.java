package org.daemawiki.domain.auth.usecase;

import org.daemawiki.domain.auth.dto.request.SignupRequest;
import reactor.core.publisher.Mono;

public interface SignupUsecase {
    Mono<Void> signup(SignupRequest request);

}
