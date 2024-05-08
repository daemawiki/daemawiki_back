package org.daemawiki.domain.document_content.model;

import lombok.Getter;

@Getter
public class Content {
    private String index;
    private String title;
    private String detail;

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeDetail(String detail) {
        this.detail = detail;
    }

    private Content(String index, String title, String detail) {
        this.index = index;
        this.title = title;
        this.detail = detail;
    }

    public static Content of(String index, String title, String detail) {
        return new Content(index, title, detail);
    }

}
