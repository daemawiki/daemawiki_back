package org.daemawiki.domain.admin.model;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Document
public class Admin {

    @MongoId
    private String email;

    private String userId = "not yet";

    protected Admin() {}

    public Admin(String email) {
        this.email = email;
    }

    public static Admin create(String email) {
        return new Admin(email);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
