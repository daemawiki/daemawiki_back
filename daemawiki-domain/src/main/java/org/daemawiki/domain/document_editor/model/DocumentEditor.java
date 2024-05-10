package org.daemawiki.domain.document_editor.model;

import lombok.Builder;
import lombok.Getter;
import org.daemawiki.domain.user.dto.response.UserDetailVo;

import java.util.List;

@Getter
@Builder
public class DocumentEditor {
    private final UserDetailVo createdUser;
    private UserDetailVo updatedUser;
    private List<Editor> canEdit;

    public boolean hasEditPermission(String email) {
        return this.canEdit.stream()
                .noneMatch(editor -> editor.user().equals(email));
    }

    public void updateUpdatedUser(UserDetailVo updatedUser) {
        this.updatedUser = updatedUser;
    }

    public void addEditor(Editor editor) {
        this.canEdit.add(editor);
    }

}
