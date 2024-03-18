package org.daemawiki.domain.auth.service;

import org.daemawiki.domain.auth.dto.request.LoginRequest;
import org.daemawiki.domain.auth.dto.response.TokenResponse;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.repository.UserRepository;
import org.daemawiki.exception.h401.PasswordMismatchException;
import org.daemawiki.exception.h404.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.daemawiki.security.Tokenizer;

@Service
public class Login {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Tokenizer tokenizer;

    public Login(UserRepository userRepository, PasswordEncoder passwordEncoder, Tokenizer tokenizer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenizer = tokenizer;
    }

    public Mono<TokenResponse> execute(LoginRequest request) {
        return userRepository.findByEmail(request.email())
                .switchIfEmpty(Mono.error(UserNotFoundException.EXCEPTION))
                .flatMap(user -> validateUserAndPassword(user, request.password()))
                .flatMap(user -> tokenizer.createToken(user.getEmail())
                        .map(TokenResponse::create));
    }

    private Mono<User> validateUserAndPassword(User user, String inputPassword) {
        return Mono.just(user)
                .filter(u -> passwordEncoder.matches(inputPassword, u.getPassword()))
                .switchIfEmpty(Mono.error(PasswordMismatchException.EXCEPTION));
    }

}
