package org.daemawiki.domain.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.daemawiki.domain.file.model.File;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
@NoArgsConstructor
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

    @Builder
    public User(String name, String email, String password, File profile, UserDetail detail, String documentId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.detail = detail;
        this.documentId = documentId;
    }

    public void update(String name, UserDetail detail) {
        this.name = name;
        this.detail = detail;
    }

}
