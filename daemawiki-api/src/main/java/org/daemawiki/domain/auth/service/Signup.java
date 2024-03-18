package org.daemawiki.domain.auth.service;

import org.daemawiki.domain.auth.dto.request.SignupRequest;
import org.daemawiki.domain.document.component.CreateDocumentByUser;
import org.daemawiki.domain.document.DefaultProfile;
import org.daemawiki.domain.mail.repository.AuthMailRepository;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.model.UserDetail;
import org.daemawiki.domain.user.repository.UserRepository;
import org.daemawiki.domain.user.type.major.component.GetMajorType;
import org.daemawiki.exception.h403.UnVerifiedEmailException;
import org.daemawiki.exception.h409.EmailAlreadyExistsException;
import org.daemawiki.exception.h500.ExecuteFailedException;
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
                .switchIfEmpty(Mono.defer(() -> checkAndCreateUser(request)))
                .then();
    }

    private Mono<Void> checkAndCreateUser(SignupRequest request) {
        return authMailRepository.findByMail(request.email())
                .filter(verified -> verified)
                .switchIfEmpty(Mono.error(UnVerifiedEmailException.EXCEPTION))
                .flatMap(verified -> Mono.fromSupplier(() -> passwordEncoder.encode(request.password()))
                        .subscribeOn(scheduler)
                        .flatMap(password -> createUser(request, password))
                        .flatMap(this::saveUserAndCreateDocument)
                        .then(authMailRepository.delete(request.email()))
                        .onErrorMap(e -> ExecuteFailedException.EXCEPTION));
    }

    private Mono<User> saveUserAndCreateDocument(User user) {
        return userRepository.save(user)
                .flatMap(savedUser -> createDocumentByUser.execute(savedUser)
                        .doOnNext(document -> savedUser.setDocumentId(document.getId()))
                        .flatMap(document -> userRepository.save(savedUser)));
    }

    private Mono<User> createUser(SignupRequest request, String password) {
        return Mono.just(User.builder()
                .name(request.name())
                .email(request.email())
                .password(password)
                .profile(defaultProfile.defaultProfile())
                .detail(UserDetail.builder()
                        .gen(request.gen())
                        .major(getMajorType.execute(request.major().toLowerCase()))
                        .club("*")
                        .build())
                .build());
    }

}
