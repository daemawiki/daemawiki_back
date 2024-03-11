package com.example.daemawiki.domain.info.model;

import lombok.Builder;

@Builder
public record Detail(
        String title,
        String content
) {

    public static Detail create(String title, String content) {
        return Detail.builder()
                .title(title)
                .content(content)
                .build();
    }

}
