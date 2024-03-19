package org.daemawiki.domain.auth.usecase.service;

import org.daemawiki.domain.auth.dto.request.ReissueRequest;
import org.daemawiki.domain.auth.dto.response.TokenResponse;
import org.daemawiki.domain.auth.usecase.ReissueUsecase;
import org.daemawiki.exception.h500.TokenReissueFailedException;
import org.daemawiki.security.Tokenizer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class Reissue implements ReissueUsecase {
    private final Tokenizer tokenizer;

    public Reissue(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public Mono<TokenResponse> reissue(ReissueRequest request) {
        return tokenizer.reissue(request.token())
                .map(TokenResponse::create)
                .switchIfEmpty(Mono.error(TokenReissueFailedException.EXCEPTION));
    }

}
