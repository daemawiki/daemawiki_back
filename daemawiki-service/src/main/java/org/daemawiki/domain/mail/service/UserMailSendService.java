package org.daemawiki.domain.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.daemawiki.domain.mail.port.code.SaveAuthCodePort;
import org.daemawiki.domain.mail.dto.AuthCodeRequest;
import org.daemawiki.domain.mail.model.AuthCode;
import org.daemawiki.domain.mail.model.type.MailType;
import org.daemawiki.domain.mail.usecase.UserMailSendUsecase;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.exception.h409.EmailAlreadyExistsException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.daemawiki.exception.h500.MailSendFailedException;
import org.daemawiki.exception.h500.RedisConnectFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

@Slf4j
@Service
public class UserMailSendService implements UserMailSendUsecase {
    private final SaveAuthCodePort saveAuthCodePort;
    private final JavaMailSender mailSender;
    private final Scheduler scheduler;
    private final FindUserPort findUserPort;

    public UserMailSendService(SaveAuthCodePort saveAuthCodePort, JavaMailSender javaMailSender, Scheduler scheduler, FindUserPort findUserPort) {
        this.saveAuthCodePort = saveAuthCodePort;
        this.mailSender = javaMailSender;
        this.scheduler = scheduler;
        this.findUserPort = findUserPort;
    }

    @Value("${admin.mail}")
    private String admin;

    @Override
    public Mono<Void> send(AuthCodeRequest request) {
        String mail = request.mail();

        return findUserPort.findByEmail(mail)
                .flatMap(user -> {
                    if (Objects.equals(request.type(), MailType.SIGNUP.getType())) {
                        return Mono.error(EmailAlreadyExistsException.EXCEPTION);
                    } else {
                        return Mono.empty();
                    }
                })
                .then(Mono.defer(() -> {
                    String authCode = getRandomCode();

                    log.info("authCode: {} to: {}", authCode, mail);

                    sendMail(mail, authCode)
                            .subscribeOn(scheduler)
                            .subscribe();

                    return saveAuthCode(mail, authCode);
                }));
    }

    private Mono<Void> sendMail(String to, String authCode) {
        return Mono.fromRunnable(() -> {
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
        }).onErrorMap(e -> MailSendFailedException.EXCEPTION).then();
    }

    private Mono<Void> saveAuthCode(String to, String authCode) {
        return saveAuthCodePort.save(AuthCode.builder()
                .mail(to)
                .code(authCode)
                .build())
                .onErrorMap(e -> e instanceof RedisConnectFailedException ? e : ExecuteFailedException.EXCEPTION);
    }

    private String getMailTemplate(String key) {
        return "<div style='margin: 10px; background-color: #f5f5f5; padding: 20px; border-radius: 10px;'>"
                + "<p style='font-size: 16px; color: #333;'><b><span style='color: #007bff;'>D</span><span style='color: #ffcc00;'>S</span><span style='color: #ff0000;'>M</span></b> 이메일 인증 코드 :</p>"
                + "<p style='font-size: 24px; font-weight: bold; color: #007bff; letter-spacing: 3px;'>" + key + "</p>"
                + "<p style='font-size: 14;font-style: italic; color: #999;'>인증 코드는 3시간 동안 유효합니다.</p>"
                + "</div>";
    }

    private String getRandomCode() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] seed = new byte[32];
        secureRandom.nextBytes(seed);
        secureRandom.setSeed(seed);
        byte[] randomBytes = new byte[4];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

}
