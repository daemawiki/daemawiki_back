package com.example.daemawiki.domain.document.model;

import com.example.daemawiki.domain.user.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DocumentEditor {

    private final User createdUser;

    private User updatedUser;

}
