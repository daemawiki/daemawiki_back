package com.example.daemawiki.global.dateTime.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EditDateTime {

    private LocalDateTime created;
    private LocalDateTime updated;

}
