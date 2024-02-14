package com.example.daemawiki.domain.user.model;

import com.example.daemawiki.infra.s3.model.FileResponse;
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

    private FileResponse profile;

    @Builder
    public User(String nickname, String email, String password, FileResponse profile) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

}
