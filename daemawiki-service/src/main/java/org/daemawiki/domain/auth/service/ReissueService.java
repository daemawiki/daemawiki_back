package org.daemawiki.domain.auth.service;

import org.daemawiki.domain.auth.dto.response.TokenResponse;
import org.daemawiki.domain.auth.usecase.ReissueUsecase;
import org.daemawiki.exception.h500.TokenReissueFailedException;
import org.daemawiki.security.Tokenizer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ReissueService implements ReissueUsecase {
    private final Tokenizer tokenizer;

    public ReissueService(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     * 토큰을 재발급 메서드
     *
     * @param token 재발급 요청 객체
     * @return 토큰 응답 객체
     * @throws TokenReissueFailedException 토큰 재발급 실패 시 예외 발생
     */
    @Override
    public Mono<TokenResponse> reissue(String token ) {
        return tokenizer.reissue(tokenParse(token))
                .map(TokenResponse::create)
                .switchIfEmpty(Mono.defer(() -> Mono.error(TokenReissueFailedException.EXCEPTION)));
    }

    private String tokenParse(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        } else {
            throw TokenReissueFailedException.EXCEPTION;
        }
    }

}
