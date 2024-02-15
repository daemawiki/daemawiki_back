package com.example.daemawiki.domain.file.model;

import com.example.daemawiki.domain.file.model.type.FileType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultProfile {
    private static final UUID id = UUID.randomUUID();
    public static final File DEFAULT_PROFILE = File.builder()
            .id(id)
            .fileName("")
            .fileType("")
            .detail(FileDetail.builder()
                    .type(FileType.PROFILE)
                    .url("")
                    .build())
            .build();

}