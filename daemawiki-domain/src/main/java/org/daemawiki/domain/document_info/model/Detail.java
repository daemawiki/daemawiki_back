package org.daemawiki.domain.document_info.model;

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
