package com.example.daemawiki.domain.user.model;

import com.example.daemawiki.domain.file.model.File;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document
@NoArgsConstructor
public class User {

    @Id
    private String id;

    private String nickname;

    private String email;

    @JsonIgnore
    private String password;

    private File profile;

    @Builder
    public User(String nickname, String email, String password, File profile) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    public void editProfile(File fileResponse) {
        this.profile = fileResponse;
    }

}
