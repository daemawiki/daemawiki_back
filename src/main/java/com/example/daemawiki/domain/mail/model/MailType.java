package com.example.daemawiki.domain.mail.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MailType {
    CHANGE_PW("change_pw"),
    SIGNUP("signup");

    private final String type;
}
