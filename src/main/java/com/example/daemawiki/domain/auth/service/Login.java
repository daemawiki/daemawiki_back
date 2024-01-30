package com.example.daemawiki.domain.auth.service;

import com.example.daemawiki.domain.auth.dto.LoginRequest;
import com.example.daemawiki.domain.auth.dto.LoginResponse;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.global.exception.PasswordMismatchException;
import com.example.daemawiki.global.exception.UserNotFoundException;
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

    public Mono<LoginResponse> execute(LoginRequest request) {
        return userRepository.findByEmail(request.email())
                .switchIfEmpty(Mono.error(UserNotFoundException.EXCEPTION))
                .flatMap(user -> Mono.just(user)
                        .filter(u -> passwordEncoder.matches(u.getPassword(), request.password()))
                        .switchIfEmpty(Mono.error(PasswordMismatchException.EXCEPTION))
                        .flatMap(u -> tokenizer.createToken(u.getEmail())
                                .map(token -> LoginResponse.builder()
                                        .token(token)
                                        .build())));
    }

}
