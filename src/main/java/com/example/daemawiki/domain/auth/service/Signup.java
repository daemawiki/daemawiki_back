package com.example.daemawiki.domain.auth.service;

import com.example.daemawiki.domain.auth.dto.request.SignupRequest;
import com.example.daemawiki.domain.document.component.CreateDocumentByUser;
import com.example.daemawiki.domain.file.model.DefaultProfile;
import com.example.daemawiki.domain.mail.repository.AuthMailRepository;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.model.UserDetail;
import com.example.daemawiki.domain.user.model.type.component.GetMajorType;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.global.exception.h403.UnVerifiedEmailException;
import com.example.daemawiki.global.exception.h409.EmailAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/*
    유저 회원가입 성공시
    해당 유저의 문서 생성
*/

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
                .switchIfEmpty(Mono.justOrEmpty(authMailRepository.findByMail(request.email())
                        .flatMap(verified -> {
                            if (!verified) {
                                return Mono.error(UnVerifiedEmailException.EXCEPTION);
                            }

                            return Mono.fromCallable(() -> passwordEncoder.encode(request.password()))
                                    .subscribeOn(scheduler)
                                    .flatMap(password -> {
                                        User newUser = User.builder()
                                                .name(request.name())
                                                .email(request.email())
                                                .password(password)
                                                .profile(defaultProfile.defaultProfile())
                                                .detail(UserDetail.builder()
                                                        .gen(request.gen())
                                                        .major(getMajorType.execute(request.major()))
                                                        .build())
                                                .build();

                                        return userRepository.save(newUser)
                                                .flatMap(user -> createDocumentByUser.execute(user)
                                                        .flatMap(document -> {
                                                            user.setDocumentId(document.getId());
                                                            return userRepository.save(user);
                                                        }));
                                    });
                        })))
                .then();
    }


}
