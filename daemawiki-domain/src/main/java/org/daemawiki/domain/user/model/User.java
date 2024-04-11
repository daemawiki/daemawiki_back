package org.daemawiki.domain.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.daemawiki.domain.file.model.File;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class User {

    @Id
    private String id;

    private String name;

    private String email;

    @JsonIgnore
    private String password;

    private File profile;

    private UserDetail detail;

    private String documentId;

    private Boolean isAdmin;

    private Boolean isBlocked = false;

    protected User() {}

    @Builder
    public User(String name, String email, String password, File profile, UserDetail detail, String documentId, Boolean isAdmin) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.detail = detail;
        this.documentId = documentId;
        this.isAdmin = isAdmin;
    }

    public void update(String name, UserDetail detail) {
        this.name = name;
        this.detail = detail;
    }

}
