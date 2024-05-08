package org.daemawiki.domain.mail.adapter;

import org.daemawiki.domain.mail.model.AuthMail;
import org.daemawiki.domain.mail.port.mail.DeleteAuthMailPort;
import org.daemawiki.domain.mail.port.mail.FindAuthMailPort;
import org.daemawiki.domain.mail.port.mail.SaveAuthMailPort;
import org.daemawiki.domain.mail.repository.AuthMailRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthMailAdapter implements FindAuthMailPort, SaveAuthMailPort, DeleteAuthMailPort {
    private final AuthMailRepository authMailRepository;

    public AuthMailAdapter(AuthMailRepository authMailRepository) {
        this.authMailRepository = authMailRepository;
    }

    @Override
    public Mono<Void> delete(String mail) {
        return authMailRepository.delete(mail)
                .then();
    }

    @Override
    public Mono<Boolean> findByMail(String mail) {
        return authMailRepository.findByMail(mail);
    }

    @Override
    public Mono<Void> save(AuthMail authMail) {
        return authMailRepository.save(authMail)
                .then();
    }
}
