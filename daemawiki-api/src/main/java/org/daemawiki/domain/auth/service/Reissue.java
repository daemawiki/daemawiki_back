package org.daemawiki.domain.auth.service;

import org.daemawiki.domain.auth.dto.request.ReissueRequest;
import org.daemawiki.domain.auth.dto.response.TokenResponse;
import org.daemawiki.exception.h500.TokenReissueFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.daemawiki.security.Tokenizer;

@Service
public class Reissue {
    private final Tokenizer tokenizer;

    public Reissue(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public Mono<TokenResponse> execute(ReissueRequest request) {
        return tokenizer.reissue(request.token())
                .map(TokenResponse::create)
                .switchIfEmpty(Mono.error(TokenReissueFailedException.EXCEPTION));
    }

}
