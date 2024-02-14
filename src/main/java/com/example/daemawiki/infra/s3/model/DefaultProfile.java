package com.example.daemawiki.infra.s3.model;

import com.example.daemawiki.infra.s3.model.type.FileType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultProfile {
    public static final FileResponse DEFAULT_PROFILE = FileResponse.builder()
            .fileName("")
            .fileType("")
            .detail(FileDetail.builder()
                    .type(FileType.PROFILE)
                    .url("")
                    .build())
            .build();

}