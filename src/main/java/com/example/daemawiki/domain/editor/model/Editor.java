package com.example.daemawiki.domain.editor.model;

import lombok.Builder;

@Builder
public record Editor(String user, String id) {

    public static Editor create(String user, String id) {
        return Editor.builder()
                .user(user)
                .id(id)
                .build();
    }

}
