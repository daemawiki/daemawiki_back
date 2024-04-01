package org.daemawiki.domain.common;

import org.daemawiki.domain.file.model.File;
import org.daemawiki.domain.file.model.FileDetail;
import org.daemawiki.domain.file.model.type.FileType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DefaultProfileImpl implements DefaultProfile {

    @Value("${profile.image.url}")
    private String defaultImageURL;

    @Value("${profile.image.id}")
    private UUID defaultImageId;

    @Value("${profile.image.name}")
    private String defaultImageName;

    @Value("${profile.image.type}")
    private String defaultImageType;

    @Override
    public final File defaultProfile() {
        return File.create(defaultImageId,
                defaultImageName,
                defaultImageType,
                FileDetail.create(FileType.PROFILE, defaultImageURL));
    }

    @Override
    public final File defaultDocumentImage() {
        return File.create(defaultImageId,
                defaultImageName,
                defaultImageType,
                FileDetail.create(FileType.DOCUMENT, defaultImageURL));
    }

}
