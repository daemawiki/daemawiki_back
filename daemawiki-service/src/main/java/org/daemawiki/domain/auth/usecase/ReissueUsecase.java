package org.daemawiki.domain.auth.usecase;

import org.daemawiki.domain.auth.dto.response.TokenResponse;
import reactor.core.publisher.Mono;

public interface ReissueUsecase {
    Mono<TokenResponse> reissue(String token);

}
