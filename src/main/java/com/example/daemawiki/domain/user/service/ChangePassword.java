package com.example.daemawiki.domain.user.service;

import com.example.daemawiki.domain.mail.repository.AuthMailRepository;
import com.example.daemawiki.domain.user.dto.ChangePasswordRequest;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h403.UnVerifiedEmailException;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChangePassword {
    private final PasswordEncoder passwordEncoder;
    private final UserFacade userFacade;
    private final UserRepository userRepository;
    private final AuthMailRepository authMailRepository;

    public ChangePassword(PasswordEncoder passwordEncoder, UserFacade userFacade, UserRepository userRepository, AuthMailRepository authMailRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userFacade = userFacade;
        this.userRepository = userRepository;
        this.authMailRepository = authMailRepository;
    }

    public Mono<Void> currentUser(ChangePasswordRequest request) {
        return userFacade.currentUser()
                .flatMap(user -> changePasswordAndSaveUser(request.newPassword(), user));
    }

    public Mono<Void> NonLoggedInUser(ChangePasswordRequest request) {
        return userFacade.findByEmailNotNull(request.email())
                .flatMap(user -> authMailRepository.findByMail(request.email())
                        .flatMap(verified -> {
                            if (!verified) {
                                return Mono.error(UnVerifiedEmailException.EXCEPTION);
                            }

                            return changePasswordAndSaveUser(request.newPassword(), user);
                        }));
    }

    private Mono<Void> changePasswordAndSaveUser(String newPassword, User user) {
        user.changePassword(passwordEncoder.encode(newPassword));

        return userRepository.save(user)
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION)
                .then();
    }

}
