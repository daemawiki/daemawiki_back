package org.daemawiki.domain.mail.usecase.service;

import org.daemawiki.domain.mail.application.code.DeleteAuthCodePort;
import org.daemawiki.domain.mail.application.code.GetAuthCodePort;
import org.daemawiki.domain.mail.application.mail.SaveAuthMailPort;
import org.daemawiki.domain.mail.dto.AuthCodeVerifyRequest;
import org.daemawiki.domain.mail.dto.AuthCodeVerifyResponse;
import org.daemawiki.domain.mail.model.AuthCode;
import org.daemawiki.domain.mail.model.AuthMail;
import org.daemawiki.domain.mail.usecase.UserMailVerifyUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserMailVerifyService implements UserMailVerifyUsecase {
    private final SaveAuthMailPort saveAuthMailPort;
    private final GetAuthCodePort getAuthCodePort;
    private final DeleteAuthCodePort deleteAuthCodePort;

    public UserMailVerifyService(SaveAuthMailPort saveAuthMailPort, GetAuthCodePort getAuthCodePort, DeleteAuthCodePort deleteAuthCodePort) {
        this.saveAuthMailPort = saveAuthMailPort;
        this.getAuthCodePort = getAuthCodePort;
        this.deleteAuthCodePort = deleteAuthCodePort;
    }

    @Override
    public Mono<ResponseEntity<AuthCodeVerifyResponse>> verify(AuthCodeVerifyRequest request) {
        String mail = request.mail();

        return getAuthCode(mail, request.authCode())
                .flatMap(authCode -> save(mail)
                        .then(deleteAuthCodePort.delete(authCode))
                        .thenReturn(ResponseEntity.status(200)
                                .body(getResponse(true))))
                .switchIfEmpty(Mono.justOrEmpty(ResponseEntity.status(401)
                                .body(getResponse(false))));
    }

    private static final String SUCCESS = "인증에 성공했습니다.";
    private static final String FAIL = "인증에 실패했습니다.";

    private AuthCodeVerifyResponse getResponse(Boolean isVerify) {
        String message = isVerify ? SUCCESS : FAIL;

        return AuthCodeVerifyResponse.builder()
                .isSuccess(isVerify)
                .message(message)
                .build();
    }

    private Mono<Void> save(String mail) {
        return saveAuthMailPort.save(AuthMail.builder()
                .mail(mail)
                .build());
    }

    private Mono<AuthCode> getAuthCode(String mail, String authCode) {
        return getAuthCodePort.findByMail(mail)
                .filter(a -> a.getCode().equals(authCode));
    }

}
