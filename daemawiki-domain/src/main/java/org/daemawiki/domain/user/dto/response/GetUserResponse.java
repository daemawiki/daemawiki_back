package org.daemawiki.domain.user.dto.response;

import org.daemawiki.domain.file.model.File;
import org.daemawiki.domain.user.model.UserDetail;
import lombok.Builder;

@Builder
public record GetUserResponse(
        String userId,
        String documentId,
        String name,
        UserDetail detail,
        File profile
) {
}
