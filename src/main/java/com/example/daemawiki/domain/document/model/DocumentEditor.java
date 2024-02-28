package com.example.daemawiki.domain.document.model;

import com.example.daemawiki.domain.user.dto.response.UserDetailResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DocumentEditor {

    private final UserDetailResponse createdUser;

    private UserDetailResponse updatedUser;

    private List<Editor> canEdit;

    public void update(UserDetailResponse updatedUser) {
        this.updatedUser = updatedUser;
    }

    public boolean canEdit(String email) {
        return this.canEdit.stream().anyMatch(editor -> editor.email().equals(email));
    }

}
