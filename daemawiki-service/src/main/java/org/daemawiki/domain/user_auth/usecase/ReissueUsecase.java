package org.daemawiki.domain.user_auth.usecase;

import org.daemawiki.domain.user_auth.dto.response.TokenResponse;
import reactor.core.publisher.Mono;

public interface ReissueUsecase {
    Mono<TokenResponse> reissue(String token);

}
