package com.example.daemawiki.domain.mail.api;

import com.example.daemawiki.domain.mail.dto.AuthCodeRequest;
import com.example.daemawiki.domain.mail.dto.AuthCodeVerifyRequest;
import com.example.daemawiki.domain.mail.dto.AuthCodeVerifyResponse;
import com.example.daemawiki.domain.mail.service.MailSend;
import com.example.daemawiki.domain.mail.service.MailVerify;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/mail")
public class MailController {
    private final MailSend mailSend;
    private final MailVerify mailVerify;

    public MailController(MailSend mailSend, MailVerify mailVerify) {
        this.mailSend = mailSend;
        this.mailVerify = mailVerify;
    }

    @PostMapping("/send")
    public Mono<Void> send(@Valid @RequestBody AuthCodeRequest request) {
        return mailSend.execute(request);
    }

    @PostMapping("/verify")
    public Mono<AuthCodeVerifyResponse> verify(@Valid @RequestBody AuthCodeVerifyRequest request) {
        return mailVerify.execute(request);
    }

    @PostMapping("/reissue")
    public Mono<Void> reissue(@Valid @RequestBody AuthCodeRequest request) {
        return mailSend.reissue(request);
    }

}
