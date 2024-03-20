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

    @Override
    public Mono<TokenResponse> signin(LoginRequest request) {
        return getUserPort.findByEmail(request.email())
                .switchIfEmpty(Mono.error(UserNotFoundException.EXCEPTION))
                .flatMap(user -> validateUserAndPassword(user, request.password()))
                .flatMap(this::generateTokenResponse);
    }

    private Mono<TokenResponse> generateTokenResponse(User user) {
        return tokenizer.createToken(user.getEmail())
                .map(TokenResponse::create);
    }

    private Mono<User> validateUserAndPassword(User user, String inputPassword) {
        return Mono.just(user)
                .filter(u -> passwordEncoder.matches(inputPassword, u.getPassword()))
                .switchIfEmpty(Mono.error(PasswordMismatchException.EXCEPTION));
    }

}
