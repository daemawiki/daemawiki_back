package com.example.daemawiki.domain.user.service;

import com.example.daemawiki.domain.user.dto.ChangePasswordRequest;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChangePassword {
    private final PasswordEncoder passwordEncoder;
    private final UserFacade userFacade;
    private final UserRepository userRepository;

    public ChangePassword(PasswordEncoder passwordEncoder, UserFacade userFacade, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userFacade = userFacade;
        this.userRepository = userRepository;
    }

    public Mono<Void> execute(ChangePasswordRequest request) {
        return userFacade.currentUser()
                //.filter(user -> passwordEncoder.matches(request.oldPassword(), user.getPassword()))
                //.switchIfEmpty(Mono.error(PasswordMismatchException.EXCEPTION))
                .flatMap(user -> {
                    user.changePassword(passwordEncoder.encode(request.newPassword()));
                    return userRepository.save(user);
                })
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION)
                .then();
    }

}
