package com.example.daemawiki.domain.content.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Content {

    private String index;

    private String title;

    private String detail;

    public static Content create(String index, String title, String detail) {
        return Content.builder()
                .index(index)
                .title(title)
                .detail(detail)
                .build();
    }

}
