package com.example.daemawiki.domain.mail.service;

import com.example.daemawiki.domain.mail.dto.AuthCodeVerifyRequest;
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

    public Mono<Boolean> execute(AuthCodeVerifyRequest request) {
        String mail = request.mail();

        return getAuthCode(mail, request.authCode())
                .flatMap(authCode -> save(mail)
                        .then(codeRepository.delete(authCode))
                        .thenReturn(true))
                .switchIfEmpty(Mono.defer(() -> Mono.just(false)));
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
