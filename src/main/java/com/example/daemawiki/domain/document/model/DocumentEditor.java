package com.example.daemawiki.domain.document.model;

import com.example.daemawiki.domain.user.dto.UserDetailResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DocumentEditor {

    private final UserDetailResponse createdUser;

    private UserDetailResponse updatedUser;

    public void update(UserDetailResponse updatedUser) {
        this.updatedUser = updatedUser;
    }

}
