package com.example.daemawiki.global.dateTime.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EditDateTime {

    private final String created;
    private String updated;

}
