package org.daemawiki.domain.user.usecase.service;

import org.daemawiki.domain.mail.application.mail.GetAuthMailPort;
import org.daemawiki.domain.user.application.GetUserPort;
import org.daemawiki.domain.user.application.SaveUserPort;
import org.daemawiki.domain.user.dto.request.ChangePasswordRequest;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.usecase.ChangeUserPasswordUsecase;
import org.daemawiki.exception.h403.UnVerifiedEmailException;
import org.daemawiki.exception.h404.UserNotFoundException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChangeUserPasswordService implements ChangeUserPasswordUsecase {
    private final GetUserPort getUserPort;
    private final SaveUserPort saveUserPort;
    private final GetAuthMailPort getAuthMailPort;
    private final PasswordEncoder passwordEncoder;

    public ChangeUserPasswordService(GetUserPort getUserPort, SaveUserPort saveUserPort, GetAuthMailPort getAuthMailPort, PasswordEncoder passwordEncoder) {
        this.getUserPort = getUserPort;
        this.saveUserPort = saveUserPort;
        this.getAuthMailPort = getAuthMailPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<Void> currentUser(ChangePasswordRequest request) {
        return getUserPort.currentUser()
                .flatMap(user -> changePasswordAndSaveUser(request.newPassword(), user));
    }

    @Override
    public Mono<Void> nonLoggedInUser(ChangePasswordRequest request) {
        return getUserPort.findByEmail(request.email())
                .switchIfEmpty(Mono.error(UserNotFoundException.EXCEPTION))
                .flatMap(user -> getAuthMailPort.findByMail(request.email())
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
                    return saveUserPort.save(user);
                })
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION)
                .then();
    }

    private Mono<String> encodePassword(String password) {
        return Mono.just(passwordEncoder.encode(password));
    }

}
