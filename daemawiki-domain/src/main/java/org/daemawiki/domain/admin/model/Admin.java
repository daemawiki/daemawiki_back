package org.daemawiki.domain.admin.model;

import lombok.Builder;
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

    @Builder
    public Admin(String email) {
        this.email = email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
