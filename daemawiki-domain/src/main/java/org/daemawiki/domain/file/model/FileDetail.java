package org.daemawiki.domain.file.model;

import org.daemawiki.domain.file.model.type.FileType;

public record FileDetail(
        FileType type,
        String url
) {

    public static FileDetail create(FileType type, String url) {
        return new FileDetail(type, url);
    }

}
