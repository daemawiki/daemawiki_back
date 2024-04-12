package org.daemawiki.domain.user.dto.response;

import org.daemawiki.domain.file.model.File;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.model.UserDetail;
import lombok.Builder;

@Builder
public record GetUserResponse(
        String userId,
        String documentId,
        String name,
        UserDetail detail,
        File profile,
        String role
) {

    public static GetUserResponse of(User user) {
        return GetUserResponse.builder()
                .userId(user.getId())
                .documentId(user.getDocumentId())
                .name(user.getName())
                .detail(user.getDetail())
                .profile(user.getProfile())
                .role(user.getRole().name())
                .build();
    }

}
