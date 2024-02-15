package com.example.daemawiki.domain.auth.service;

import com.example.daemawiki.domain.auth.dto.request.ReissueRequest;
import com.example.daemawiki.domain.auth.dto.response.TokenResponse;
import com.example.daemawiki.global.exception.h500.TokenReissueFailedException;
import com.example.daemawiki.global.security.Tokenizer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class Reissue {
    private final Tokenizer tokenizer;

    public Reissue(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public Mono<TokenResponse> execute(ReissueRequest request) {
        return tokenizer.reissue(request.token())
                .map(token -> TokenResponse.builder()
                        .token(token)
                        .build())
                .switchIfEmpty(Mono.error(TokenReissueFailedException.EXCEPTION));
    }

}
