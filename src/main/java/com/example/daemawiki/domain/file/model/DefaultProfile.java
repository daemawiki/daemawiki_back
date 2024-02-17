package com.example.daemawiki.domain.file.model;

import com.example.daemawiki.domain.file.model.type.FileType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultProfile {

    @Value("${profile.image.url}")
    private static String defaultImageURL;

    @Value("${profile.image.id}")
    private static UUID defaultImageId;

    @Value("${profile.image.name}")
    private static String defaultImageName;

    @Value("${profile.image.type}")
    private static String defaultImageType;

    public static final File DEFAULT_PROFILE = File.builder()
            .id(defaultImageId)
            .fileName(defaultImageName)
            .fileType(defaultImageType)
            .detail(FileDetail.builder()
                    .type(FileType.PROFILE)
                    .url(defaultImageURL)
                    .build())
            .build();

}