package org.daemawiki.domain.auth.usecase.service;

import org.daemawiki.domain.auth.dto.request.SignupRequest;
import org.daemawiki.domain.auth.usecase.SignupUsecase;
import org.daemawiki.domain.common.DefaultProfile;
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

    /**
     * 이메일로 회원가입 가능 여부를 확인하고 생성하는 메서드.
     *
     * @param request 사용자가 요청한 정보
     * @return Mono<Void> 사용자 생성 작업의 결과
     */
    private Mono<Void> checkAndCreateUser(SignupRequest request) {
        return getAuthMailPort.findByMail(request.email())
                .filter(verified -> verified)
                .switchIfEmpty(Mono.error(UnVerifiedEmailException.EXCEPTION))
                .map(verified -> passwordEncoder.encode(request.password()))
                .subscribeOn(scheduler)
                .flatMap(password -> createUser(request, password))
                .flatMap(this::saveUserAndCreateDocument)
                .then(deleteAuthMailPort.delete(request.email()))
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

    /**
     * 유저를 저장하고 문서를 생성하는 메서드.
     *
     * @param user 저장할 유저
     * @return 저장된 유저
     */
    private Mono<User> saveUserAndCreateDocument(User user) {
        return saveUserPort.save(user)
                .flatMap(savedUser -> createDocumentUsecase.createByUser(savedUser)
                        .doOnNext(document -> savedUser.setDocumentId(document.getId()))
                        .flatMap(document -> saveUserPort.save(savedUser)));
    }

    /**
     * 사용자 객체를 생성하는 메서드.
     *
     * @param request 회원 가입 요청 정보
     * @param password 비밀번호
     * @return 생성된 사용자 Mono 객체
     */
    private Mono<User> createUser(SignupRequest request, String password) {
        return Mono.fromSupplier(() -> User.builder()
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
