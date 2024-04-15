package org.daemawiki.domain.user.dto.request;

import org.daemawiki.domain.user.model.UserDetail;

public record UpdateUserRequest(
        String name,
        UserDetail detail
) {
}
