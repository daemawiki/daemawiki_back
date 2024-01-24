package com.example.daemawiki.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {
    @Value("${spring.mail.smtp.port}")
    private int port;
    @Value("${spring.mail.smtp.port}")
    private int socketPort;
    @Value("${spring.mail.smtp.auth}")
    private boolean auth;
    @Value("${spring.mail.smtp.starttls.enable}")
    private boolean starttls;
    @Value("${spring.mail.smtp.starttls.required}")
    private boolean starttlsRequired;
    @Value("${spring.mail.smtp.socketFactory.fallback}")
    private boolean fallback;
    @Value("${spring.mail.smtp.socketFactory.class}")
    private String factoryClass;
    @Value("${admin.mail}")
    private String mail;
    @Value("${admin.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setUsername(mail);
        javaMailSender.setPassword(password);
        javaMailSender.setPort(port);
        javaMailSender.setJavaMailProperties(getMailProperties());
        javaMailSender.setDefaultEncoding("UTF-8");
        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties pt = new Properties();
        pt.put("mail.smtp.socketFactory.port", socketPort);
        pt.put("mail.smtp.auth", auth);
        pt.put("mail.smtp.starttls.enable", starttls);
        pt.put("mail.smtp.starttls.required", starttlsRequired);
        pt.put("mail.smtp.socketFactory.fallback", fallback);
        pt.put("mail.smtp.socketFactory.class", factoryClass);
        pt.put("mail.smtp.ssl.checkserveridentity", "true");
        return pt;
    }

}