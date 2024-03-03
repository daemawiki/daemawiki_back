package com.example.daemawiki.domain.editor.model;

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

    public boolean hasEditPermission(String email) {
        return this.canEdit.stream().noneMatch(editor -> editor.user().equals(email));
    }

    public void addEditor(Editor editor) {
        this.canEdit.add(editor);
    }

}
