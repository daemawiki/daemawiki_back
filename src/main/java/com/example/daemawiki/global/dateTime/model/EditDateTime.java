package com.example.daemawiki.global.dateTime.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EditDateTime {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "Asia/Seoul")
    private LocalDateTime created;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "Asia/Seoul")
    private LocalDateTime updated;

}
