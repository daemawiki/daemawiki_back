package com.example.daemawiki.domain.mail.service;

import com.example.daemawiki.domain.mail.dto.AuthCodeVerifyRequest;
import com.example.daemawiki.domain.mail.dto.AuthCodeVerifyResponse;
import com.example.daemawiki.domain.mail.model.AuthCode;
import com.example.daemawiki.domain.mail.model.AuthMail;
import com.example.daemawiki.domain.mail.repository.AuthCodeRepository;
import com.example.daemawiki.domain.mail.repository.AuthMailRepository;
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

    public Mono<AuthCodeVerifyResponse> execute(AuthCodeVerifyRequest request) {
        String mail = request.mail();

        return getAuthCode(mail, request.authCode())
                .flatMap(authCode -> save(mail)
                        .then(codeRepository.delete(authCode))
                        .thenReturn(getResponse(true)))
                .switchIfEmpty(Mono.defer(() -> Mono.just(getResponse(false))));
    }

    private static final String SUCCESS = "인증에 성공했습니다.";
    private static final String FAIL = "인증에 실패했습니다.";

    private AuthCodeVerifyResponse getResponse(Boolean bool) {
        String message = bool ? SUCCESS : FAIL;

        return AuthCodeVerifyResponse.builder()
                .isSuccess(bool)
                .message(message)
                .build();
    }

    private Mono<AuthMail> save(String mail) {
        return mailRepository.save(AuthMail.builder()
                .mail(mail)
                .build());
    }

    private Mono<AuthCode> getAuthCode(String mail, String authCode) {
        return codeRepository.findByMailAndCode(mail, authCode);
    }

}
