package org.daemawiki.domain.file.model;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;

@Getter
@Document
public class File {

    @MongoId
    private UUID id;

    private String fileName;

    private String fileType;

    private FileDetail detail;

    protected File() {}

    public File(UUID id, String fileName, String fileType, FileDetail detail) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.detail = detail;
    }

    public static File create(UUID id, String fileName, String fileType, FileDetail detail) {
        return new File(id, fileName, fileType, detail);
    }

}
