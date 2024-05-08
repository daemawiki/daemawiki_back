package org.daemawiki.domain.document_editor.model;

import lombok.Builder;
import lombok.Getter;
import org.daemawiki.domain.user.dto.response.UserDetailResponse;

import java.util.List;

@Getter
@Builder
public class DocumentEditor {
    private final UserDetailResponse createdUser;
    private UserDetailResponse updatedUser;
    private List<Editor> canEdit;

    public boolean hasEditPermission(String email) {
        return this.canEdit.stream()
                .noneMatch(editor -> editor.user().equals(email));
    }

    public void addEditor(Editor editor) {
        this.canEdit.add(editor);
    }

}
