package org.daemawiki.domain.user.service;

import org.daemawiki.domain.mail.repository.AuthMailRepository;
import org.daemawiki.domain.user.dto.request.ChangePasswordRequest;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.repository.UserRepository;
import org.daemawiki.domain.user.service.facade.UserFacade;
import org.daemawiki.exception.h403.UnVerifiedEmailException;
import org.daemawiki.exception.h500.ExecuteFailedException;
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

    public Mono<Void> nonLoggedInUser(ChangePasswordRequest request) {
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
        return encodePassword(newPassword)
                .flatMap(password -> {
                    user.setPassword(password);
                    return userRepository.save(user);
                })
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION)
                .then();
    }

    private Mono<String> encodePassword(String password) {
        return Mono.just(passwordEncoder.encode(password));
    }

}
