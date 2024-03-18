package org.daemawiki.domain.document;

import org.daemawiki.domain.file.model.File;
import org.daemawiki.domain.file.model.FileDetail;
import org.daemawiki.domain.file.model.type.FileType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DefaultProfile {

    @Value("${profile.image.url}")
    private String defaultImageURL;

    @Value("${profile.image.id}")
    private UUID defaultImageId;

    @Value("${profile.image.name}")
    private String defaultImageName;

    @Value("${profile.image.type}")
    private String defaultImageType;

    public final File defaultProfile() {
        return File.create(defaultImageId,
                defaultImageName,
                defaultImageType,
                FileDetail.create(FileType.PROFILE, defaultImageURL));
    }

    public final File defaultDocumentImage() {
        return File.create(defaultImageId,
                defaultImageName,
                defaultImageType,
                FileDetail.create(FileType.DOCUMENT, defaultImageURL));
    }

}
