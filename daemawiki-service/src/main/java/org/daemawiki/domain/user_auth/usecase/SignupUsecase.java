package org.daemawiki.domain.user_auth.usecase;

import org.daemawiki.domain.user_auth.dto.request.SignupRequest;
import reactor.core.publisher.Mono;

public interface SignupUsecase {
    Mono<Void> signup(SignupRequest request);

}
