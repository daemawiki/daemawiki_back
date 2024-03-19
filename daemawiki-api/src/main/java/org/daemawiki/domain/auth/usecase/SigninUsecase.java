package org.daemawiki.domain.auth.usecase;

import org.daemawiki.domain.auth.dto.request.LoginRequest;
import org.daemawiki.domain.auth.dto.response.TokenResponse;
import reactor.core.publisher.Mono;

public interface SigninUsecase {
    Mono<TokenResponse> signin(LoginRequest request);

}
