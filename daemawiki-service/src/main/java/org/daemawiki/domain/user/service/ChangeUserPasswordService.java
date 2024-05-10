package org.daemawiki.domain.user.service;

import org.daemawiki.domain.mail.port.mail.FindAuthMailPort;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.port.SaveUserPort;
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
    private final FindUserPort findUserPort;
    private final SaveUserPort saveUserPort;
    private final FindAuthMailPort findAuthMailPort;
    private final PasswordEncoder passwordEncoder;

    public ChangeUserPasswordService(FindUserPort findUserPort, SaveUserPort saveUserPort, FindAuthMailPort findAuthMailPort, PasswordEncoder passwordEncoder) {
        this.findUserPort = findUserPort;
        this.saveUserPort = saveUserPort;
        this.findAuthMailPort = findAuthMailPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<Void> currentUser(ChangePasswordRequest request) {
        return findUserPort.currentUser()
                .flatMap(user -> changePasswordAndSaveUser(request.newPassword(), user));
    }

    @Override
    public Mono<Void> nonLoggedInUser(ChangePasswordRequest request) {
        return findUserPort.findByEmail(request.email())
                .switchIfEmpty(Mono.defer(() -> Mono.error(UserNotFoundException.EXCEPTION)))
                .flatMap(user -> findAuthMailPort.findByMail(request.email())
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
                    user.updatePassword(password);
                    return saveUserPort.save(user);
                })
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION)
                .then();
    }

    private Mono<String> encodePassword(String password) {
        return Mono.just(passwordEncoder.encode(password));
    }

}
