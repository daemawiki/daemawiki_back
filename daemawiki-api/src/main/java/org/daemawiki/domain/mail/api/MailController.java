package org.daemawiki.domain.mail.api;

import jakarta.validation.Valid;
import org.daemawiki.domain.mail.dto.AuthCodeRequest;
import org.daemawiki.domain.mail.dto.AuthCodeVerifyRequest;
import org.daemawiki.domain.mail.dto.AuthCodeVerifyResponse;
import org.daemawiki.domain.mail.usecase.UserMailSendUsecase;
import org.daemawiki.domain.mail.usecase.UserMailVerifyUsecase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/mail")
public class MailController {
    private final UserMailSendUsecase userMailSendUsecase;
    private final UserMailVerifyUsecase userMailVerifyUsecase;

    public MailController(UserMailSendUsecase userMailSendUsecase, UserMailVerifyUsecase userMailVerifyUsecase) {
        this.userMailSendUsecase = userMailSendUsecase;
        this.userMailVerifyUsecase = userMailVerifyUsecase;
    }

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> send(@Valid @RequestBody AuthCodeRequest request) {
        return userMailSendUsecase.send(request);
    }

    @PostMapping("/verify")
    public Mono<ResponseEntity<AuthCodeVerifyResponse>> verify(@Valid @RequestBody AuthCodeVerifyRequest request) {
        return userMailVerifyUsecase.verify(request);
    }

}
