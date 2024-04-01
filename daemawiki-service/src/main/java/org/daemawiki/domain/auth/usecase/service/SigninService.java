package org.daemawiki.domain.auth.usecase.service;

import org.daemawiki.domain.auth.dto.request.LoginRequest;
import org.daemawiki.domain.auth.dto.response.TokenResponse;
import org.daemawiki.domain.auth.usecase.SigninUsecase;
import org.daemawiki.domain.user.application.GetUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h401.PasswordMismatchException;
import org.daemawiki.exception.h404.UserNotFoundException;
import org.daemawiki.security.Tokenizer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SigninService implements SigninUsecase {
    private final GetUserPort getUserPort;
    private final PasswordEncoder passwordEncoder;
    private final Tokenizer tokenizer;

    public SigninService(GetUserPort getUserPort, PasswordEncoder passwordEncoder, Tokenizer tokenizer) {
        this.getUserPort = getUserPort;
        this.passwordEncoder = passwordEncoder;
        this.tokenizer = tokenizer;
    }

    /**
     * 사용자 로그인 메서드
     *
     * @param request 로그인 요청 객체
     * @return 토큰 응답 Mono
     */
    @Override
    public Mono<TokenResponse> signin(LoginRequest request) {
        return getUserPort.findByEmail(request.email())
                .switchIfEmpty(Mono.error(UserNotFoundException.EXCEPTION))
                .flatMap(user -> validateUserWithPassword(user, request.password()))
                .flatMap(this::generateTokenResponse);
    }

    /**
     * 유저 토큰 생성
     *
     * @param user 토큰을 생성할 유저
     * @return 토큰 응답 Mono
     */
    private Mono<TokenResponse> generateTokenResponse(User user) {
        return tokenizer.createToken(user.getEmail())
                .map(TokenResponse::create);
    }

    /**
     * 사용자와 입력된 비밀번호를 검증.
     *
     * @param user 사용자 정보
     * @param inputPassword 입력된 비밀번호
     * @return Mono<User> 사용자 정보 Mono
     */
    private Mono<User> validateUserWithPassword(User user, String inputPassword) {
        return Mono.just(user)
                .filter(u -> passwordEncoder.matches(inputPassword, u.getPassword()))
                .switchIfEmpty(Mono.error(PasswordMismatchException.EXCEPTION));
    }

}
