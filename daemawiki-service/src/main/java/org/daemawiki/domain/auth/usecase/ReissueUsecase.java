package org.daemawiki.domain.auth.usecase;

import org.daemawiki.domain.auth.dto.request.ReissueRequest;
import org.daemawiki.domain.auth.dto.response.TokenResponse;
import reactor.core.publisher.Mono;

public interface ReissueUsecase {
    Mono<TokenResponse> reissue(ReissueRequest request);

}
