package org.daemawiki.domain.auth.usecase.service;

import org.daemawiki.domain.auth.dto.request.ReissueRequest;
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
     * @param request 재발급 요청 객체
     * @return 토큰 응답 객체
     * @throws TokenReissueFailedException 토큰 재발급 실패 시 예외 발생
     */
    @Override
    public Mono<TokenResponse> reissue(ReissueRequest request) {
        return tokenizer.reissue(request.token())
                .map(TokenResponse::create)
                .switchIfEmpty(Mono.error(TokenReissueFailedException.EXCEPTION));
    }

}
