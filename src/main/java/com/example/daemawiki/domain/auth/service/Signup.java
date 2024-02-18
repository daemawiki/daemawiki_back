package com.example.daemawiki.domain.auth.service;

import com.example.daemawiki.domain.auth.dto.request.SignupRequest;
import com.example.daemawiki.domain.document.component.CreateDocumentByUser;
import com.example.daemawiki.domain.file.model.DefaultProfile;
import com.example.daemawiki.domain.mail.repository.AuthMailRepository;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.model.UserDetail;
import com.example.daemawiki.domain.user.model.type.major.component.GetMajorType;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.global.exception.h403.UnVerifiedEmailException;
import com.example.daemawiki.global.exception.h409.EmailAlreadyExistsException;
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
    private final CreateDocumentByUser createDocumentByUser;
    private final DefaultProfile defaultProfile;

    public Signup(UserRepository userRepository,AuthMailRepository authMailRepository, PasswordEncoder passwordEncoder, Scheduler scheduler, GetMajorType getMajorType, CreateDocumentByUser createDocumentByUser, DefaultProfile defaultProfile) {
        this.userRepository = userRepository;
        this.authMailRepository = authMailRepository;
        this.passwordEncoder = passwordEncoder;
        this.scheduler = scheduler;
        this.getMajorType = getMajorType;
        this.createDocumentByUser = createDocumentByUser;
        this.defaultProfile = defaultProfile;
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
                                        .flatMap(password -> {
                                            User user = User.builder()
                                                    .name(request.name())
                                                    .email(request.email())
                                                    .password(password)
                                                    .profile(defaultProfile.defaultProfile())
                                                    .detail(UserDetail.builder()
                                                            .gen(request.gen())
                                                            .major(getMajorType.execute(request.major()))
                                                            .club(request.club())
                                                            .build())
                                                    .build();

                                            return userRepository.save(user)
                                                    .flatMap(savedUser -> createDocumentByUser.execute(savedUser)
                                                            .flatMap(document -> {
                                                                savedUser.setDocumentId(document.getId());
                                                                return userRepository.save(savedUser);
                                                            }));
                                        });
                            }))).then();
    }

}
