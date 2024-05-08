package org.daemawiki.domain.document_editor.model;

public record Editor(String user, String id) {

    public static Editor of(String user, String id) {
        return new Editor(user, id);
    }

}
