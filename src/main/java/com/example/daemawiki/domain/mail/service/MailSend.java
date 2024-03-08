package com.example.daemawiki.domain.mail.service;

import com.example.daemawiki.domain.mail.dto.AuthCodeRequest;
import com.example.daemawiki.domain.mail.model.AuthCode;
import com.example.daemawiki.domain.mail.model.type.MailType;
import com.example.daemawiki.domain.mail.repository.AuthCodeRepository;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h409.EmailAlreadyExistsException;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import com.example.daemawiki.global.exception.h500.MailSendFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class MailSend {
    private final AuthCodeRepository codeRepository;
    private final JavaMailSender mailSender;
    private final Scheduler scheduler;
    private final UserFacade userFacade;

    public MailSend(AuthCodeRepository authCodeRepository, JavaMailSender javaMailSender, Scheduler scheduler, UserFacade userFacade) {
        this.codeRepository = authCodeRepository;
        this.mailSender = javaMailSender;
        this.scheduler = scheduler;
        this.userFacade = userFacade;
    }

    @Value("${admin.mail}")
    private String admin;

    private static final Random rand = new Random();

    public Mono<Void> execute(AuthCodeRequest request) {
        String mail = request.mail();

        Mono<User> userMono = userFacade.findByEmail(mail)
                .flatMap(user -> {
                    if (Objects.equals(request.type(), MailType.CHANGE_PW.getType())) {
                        return Mono.empty();
                    }

                    return Mono.error(EmailAlreadyExistsException.EXCEPTION);
                });

        String authCode = getRandomCode();

        Mono<Void> sendMailMono = sendMail(mail, authCode).subscribeOn(scheduler);
        Mono<Void> saveAuthCodeMono = saveAuthCode(mail, authCode).subscribeOn(scheduler);

        return Mono.when(sendMailMono, saveAuthCodeMono, userMono).then();
    }

    private Mono<Void> sendMail(String to, String authCode) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(to);
                helper.setSubject("DSM 메일 인증");

                String mail = getMailTemplate(authCode);

                helper.setText(mail, true);
                helper.setFrom(new InternetAddress(admin, "DSM-MAIL-AUTH"));

                mailSender.send(message);
            } catch (MessagingException | UnsupportedEncodingException e) {
                throw MailSendFailedException.EXCEPTION;
            }
        });

        return Mono.fromFuture(future)
                .onErrorMap(e -> MailSendFailedException.EXCEPTION)
                .then();
    }

    private Mono<Void> saveAuthCode(String to, String authCode) {
        return codeRepository.save(AuthCode.builder()
                .mail(to)
                .code(authCode)
                .build())
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

    private String getMailTemplate(String key) {
        return "<div style='margin: 10px; background-color: #f5f5f5; padding: 20px; border-radius: 10px;'>"
                + "<p style='font-size: 16px; color: #333;'><b><span style='color: #007bff;'>D</span><span style='color: #ffcc00;'>S</span><span style='color: #ff0000;'>M</span></b> 이메일 인증 코드 :</p>"
                + "<p style='font-size: 24px; font-weight: bold; color: #007bff; letter-spacing: 3px;'>" + key + "</p>"
                + "<p style='font-size: 14;font-style: italic; color: #999;'>인증 코드는 3시간 동안 유효합니다.</p>"
                + "</div>";
    }

    private String getRandomCode() {
        int num = rand.nextInt(900000) + 100000;
        return String.valueOf(num);
    }

}
