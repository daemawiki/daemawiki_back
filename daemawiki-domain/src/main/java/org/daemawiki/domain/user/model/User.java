package org.daemawiki.domain.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.daemawiki.domain.file.model.File;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
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

    private Role role;

    private Boolean isBlocked = false;

    @CreatedDate
    private LocalDateTime createdAt;

    protected User() {}

    @Builder
    public User(String name, String email, String password, File profile, UserDetail detail, String documentId, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.detail = detail;
        this.documentId = documentId;
        this.role = role;
    }

    public void update(String name, UserDetail detail) {
        this.name = name;
        this.detail = detail;
    }

    public void updateDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void updateProfile(File profile) {
        this.profile = profile;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setIsBlocked(Boolean blocked) {
        this.isBlocked = blocked;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public enum Role {
        USER,
        ADMIN,
        OPERATOR,
        MANAGER
    }
}
