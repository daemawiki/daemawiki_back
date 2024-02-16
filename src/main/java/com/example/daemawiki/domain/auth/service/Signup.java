package com.example.daemawiki.domain.auth.service;

import com.example.daemawiki.domain.auth.dto.request.SignupRequest;
import com.example.daemawiki.domain.mail.repository.AuthMailRepository;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.model.UserDetail;
import com.example.daemawiki.domain.user.model.type.component.GetMajorType;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.global.exception.h409.EmailAlreadyExistsException;
import com.example.daemawiki.global.exception.h403.UnVerifiedEmailException;
import com.example.daemawiki.domain.file.model.DefaultProfile;
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
    private final GetMajorType getMajorType;

    public Signup(UserRepository userRepository,AuthMailRepository authMailRepository, PasswordEncoder passwordEncoder, Scheduler scheduler, GetMajorType getMajorType) {
        this.userRepository = userRepository;
        this.authMailRepository = authMailRepository;
        this.passwordEncoder = passwordEncoder;
        this.scheduler = scheduler;
        this.getMajorType = getMajorType;
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
                                                    .name(request.name())
                                                    .email(request.email())
                                                    .password(password)
                                                    .profile(DefaultProfile.DEFAULT_PROFILE)
                                                    .detail(UserDetail.builder()
                                                            .gen(request.gen())
                                                            .major(getMajorType.execute(request.major()))
                                                            .build())
                                                    .build())
                                        .flatMap(userRepository::save);
                            }))).then();
    }

}
