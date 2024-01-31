package com.example.daemawiki.domain.auth.service;

import com.example.daemawiki.domain.auth.dto.SignupRequest;
import com.example.daemawiki.domain.mail.repository.AuthMailRepository;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.global.exception.H409.EmailAlreadyExistsException;
import com.example.daemawiki.global.exception.H403.UnVerifiedEmailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service
public class Signup {
    private final UserRepository userRepository;
    private final AuthMailRepository authMailRepository;
    private final PasswordEncoder passwordEncoder;
    private final Scheduler scheduler;

    public Signup(UserRepository userRepository,AuthMailRepository authMailRepository, PasswordEncoder passwordEncoder, Scheduler scheduler) {
        this.userRepository = userRepository;
        this.authMailRepository = authMailRepository;
        this.passwordEncoder = passwordEncoder;
        this.scheduler = scheduler;
    }

    public Mono<Void> execute(SignupRequest request) {
        return userRepository.findByEmail(request.email())
                .flatMap(user -> Mono.error(EmailAlreadyExistsException.EXCEPTION))
                .switchIfEmpty(Mono.defer(() -> authMailRepository.findByMail(request.email())
                            .flatMap(verified -> {
                                if (!verified) {
                                    return Mono.error(UnVerifiedEmailException.EXCEPTION);
                                }

                                return Mono.fromCallable(() -> passwordEncoder.encode(request.password()))
                                        .subscribeOn(scheduler)
                                        .map(password -> User.builder()
                                                    .nickname(request.nickname())
                                                    .email(request.email())
                                                    .password(password)
                                                    .build())
                                        .flatMap(userRepository::save);
                            }))).then();
    }

}
