package org.daemawiki.config;

import lombok.Getter;
import org.daemawiki.domain.file.model.File;
import org.daemawiki.domain.file.model.FileDetail;
import org.daemawiki.domain.file.model.type.FileType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Getter
@Component
@ConfigurationProperties(prefix = "profile.image")
public class DefaultProfileConfig {
    private String url;
    private UUID id;
    private String name;
    private String type;

    public final File defaultUserProfile() {
        return File.create(id,
                name,
                type,
                FileDetail.create(FileType.PROFILE, url));
    }

    public final File defaultDocumentImage() {
        return File.create(id,
                name,
                type,
                FileDetail.create(FileType.DOCUMENT, url));
    }

}
