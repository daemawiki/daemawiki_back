package org.daemawiki.domain.file.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;

@Getter
@Document
@NoArgsConstructor
public class File {

    @MongoId
    private UUID id;

    private String fileName;

    private String fileType;

    private FileDetail detail;

    @Builder
    public File(UUID id, String fileName, String fileType, FileDetail detail) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.detail = detail;
    }

    public static File create(UUID id, String fileName, String fileType, FileDetail detail) {
        return File.builder()
                .id(id)
                .fileName(fileName)
                .fileType(fileType)
                .detail(detail)
                .build();
    }

}
