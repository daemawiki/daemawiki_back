package org.daemawiki.domain.mail.service;

import org.daemawiki.domain.mail.dto.AuthCodeVerifyRequest;
import org.daemawiki.domain.mail.dto.AuthCodeVerifyResponse;
import org.daemawiki.domain.mail.model.AuthCode;
import org.daemawiki.domain.mail.model.AuthMail;
import org.daemawiki.domain.mail.repository.AuthCodeRepository;
import org.daemawiki.domain.mail.repository.AuthMailRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MailVerify {
    private final AuthMailRepository mailRepository;
    private final AuthCodeRepository codeRepository;

    public MailVerify(AuthMailRepository authMailRepository, AuthCodeRepository authCodeRepository) {
        this.mailRepository = authMailRepository;
        this.codeRepository = authCodeRepository;
    }

    public Mono<ResponseEntity<AuthCodeVerifyResponse>> execute(AuthCodeVerifyRequest request) {
        String mail = request.mail();

        return getAuthCode(mail, request.authCode())
                .flatMap(authCode -> save(mail)
                        .then(codeRepository.delete(authCode))
                        .thenReturn(ResponseEntity.status(200)
                                .body(getResponse(true))))
                .switchIfEmpty(Mono.justOrEmpty(ResponseEntity.status(400)
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
        return mailRepository.save(AuthMail.builder()
                .mail(mail)
                .build());
    }

    private Mono<AuthCode> getAuthCode(String mail, String authCode) {
        return codeRepository.findByMail(mail)
                .filter(a -> a.getCode().equals(authCode));
    }

}
