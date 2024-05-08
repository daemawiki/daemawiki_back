package org.daemawiki.domain.mail.adapter;

import org.daemawiki.domain.mail.model.AuthCode;
import org.daemawiki.domain.mail.port.code.DeleteAuthCodePort;
import org.daemawiki.domain.mail.port.code.FindAuthCodePort;
import org.daemawiki.domain.mail.port.code.SaveAuthCodePort;
import org.daemawiki.domain.mail.repository.AuthCodeRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthCodeAdapter implements FindAuthCodePort, DeleteAuthCodePort, SaveAuthCodePort {
    private final AuthCodeRepository authCodeRepository;

    public AuthCodeAdapter(AuthCodeRepository authCodeRepository) {
        this.authCodeRepository = authCodeRepository;
    }

    @Override
    public Mono<Void> delete(AuthCode authCode) {
        return authCodeRepository.delete(authCode)
                .then();
    }

    @Override
    public Mono<AuthCode> findByMail(String mail) {
        return authCodeRepository.findByMail(mail);
    }

    @Override
    public Mono<Void> save(AuthCode authCode) {
        return authCodeRepository.save(authCode)
                .then();
    }
}
