package org.daemawiki.domain.document_info.model;

public record Detail(
        String title,
        String content
) {

    public static Detail of(String title, String content) {
        return new Detail(title, content);
    }

}
