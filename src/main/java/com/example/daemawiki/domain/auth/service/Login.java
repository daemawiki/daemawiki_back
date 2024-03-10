package com.example.daemawiki.domain.auth.service;

import com.example.daemawiki.domain.auth.dto.request.LoginRequest;
import com.example.daemawiki.domain.auth.dto.response.TokenResponse;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.global.exception.h401.PasswordMismatchException;
import com.example.daemawiki.global.exception.h404.UserNotFoundException;
import com.example.daemawiki.global.security.Tokenizer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
                .flatMap(user -> Mono.just(user)
                        .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                        .switchIfEmpty(Mono.error(PasswordMismatchException.EXCEPTION))
                        .flatMap(u -> tokenizer.createToken(u.getEmail())
                                .map(TokenResponse::create)));
    }

}
