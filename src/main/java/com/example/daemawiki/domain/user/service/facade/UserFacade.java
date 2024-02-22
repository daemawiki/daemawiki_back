package com.example.daemawiki.domain.user.service.facade;

import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.global.exception.h404.UserNotFoundException;
import lombok.NonNull;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Component
public class UserFacade {
    private final UserRepository userRepository;

    public UserFacade(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> currentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(this::findByEmailNotNull);
    }

    public Mono<User> findByEmailNotNull(String email) {
        return findByEmail(email)
                .switchIfEmpty(Mono.error(UserNotFoundException.EXCEPTION));
    }

    public Mono<User> findByEmail(@NonNull String email) {
        return userRepository.findByEmail(email);
    }

}