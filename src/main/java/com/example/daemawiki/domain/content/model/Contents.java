package com.example.daemawiki.domain.content.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Contents {

    private String index;

    private String title;

    private String content;

}
