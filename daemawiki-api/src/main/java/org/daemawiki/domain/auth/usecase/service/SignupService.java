package org.daemawiki.domain.auth.usecase.service;

import org.daemawiki.domain.auth.dto.request.SignupRequest;
import org.daemawiki.domain.auth.usecase.SignupUsecase;
import org.daemawiki.domain.common.DefaultProfile;
import org.daemawiki.domain.common.DefaultProfileImpl;
import org.daemawiki.domain.document.usecase.CreateDocumentUsecase;
import org.daemawiki.domain.mail.application.mail.DeleteAuthMailPort;
import org.daemawiki.domain.mail.application.mail.GetAuthMailPort;
import org.daemawiki.domain.user.application.GetUserPort;
import org.daemawiki.domain.user.application.SaveUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.model.UserDetail;
import org.daemawiki.domain.user.type.major.component.GetMajorType;
import org.daemawiki.exception.h403.UnVerifiedEmailException;
import org.daemawiki.exception.h409.EmailAlreadyExistsException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service
public class SignupService implements SignupUsecase {
    private final GetUserPort getUserPort;
    private final SaveUserPort saveUserPort;
    private final GetAuthMailPort getAuthMailPort;
    private final DeleteAuthMailPort deleteAuthMailPort;
    private final PasswordEncoder passwordEncoder;
    private final Scheduler scheduler;
    private final GetMajorType getMajorType;
    private final CreateDocumentUsecase createDocumentUsecase;
    private final DefaultProfile defaultProfile;

    public SignupService(GetUserPort getUserPort, SaveUserPort saveUserPort, GetAuthMailPort getAuthMailPort, DeleteAuthMailPort deleteAuthMailPort, PasswordEncoder passwordEncoder, Scheduler scheduler, GetMajorType getMajorType, CreateDocumentUsecase createDocumentUsecase, DefaultProfile defaultProfile) {
        this.getUserPort = getUserPort;
        this.saveUserPort = saveUserPort;
        this.getAuthMailPort = getAuthMailPort;
        this.passwordEncoder = passwordEncoder;
        this.scheduler = scheduler;
        this.getMajorType = getMajorType;
        this.deleteAuthMailPort = deleteAuthMailPort;
        this.createDocumentUsecase = createDocumentUsecase;
        this.defaultProfile = defaultProfile;
    }

    @Override
    public Mono<Void> signup(SignupRequest request) {
        return getUserPort.findByEmail(request.email())
                .flatMap(user -> Mono.error(EmailAlreadyExistsException.EXCEPTION))
                .switchIfEmpty(Mono.defer(() -> checkAndCreateUser(request)))
                .then();
    }

    private Mono<Void> checkAndCreateUser(SignupRequest request) {
        return getAuthMailPort.findByMail(request.email())
                .filter(verified -> verified)
                .switchIfEmpty(Mono.error(UnVerifiedEmailException.EXCEPTION))
                .flatMap(verified -> Mono.fromSupplier(() -> passwordEncoder.encode(request.password()))
                        .subscribeOn(scheduler)
                        .flatMap(password -> createUser(request, password))
                        .flatMap(this::saveUserAndCreateDocument)
                        .then(deleteAuthMailPort.delete(request.email()))
                        .onErrorMap(e -> ExecuteFailedException.EXCEPTION));
    }

    private Mono<User> saveUserAndCreateDocument(User user) {
        return saveUserPort.save(user)
                .flatMap(savedUser -> createDocumentUsecase.createByUser(savedUser)
                        .doOnNext(document -> savedUser.setDocumentId(document.getId()))
                        .flatMap(document -> saveUserPort.save(savedUser)));
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
