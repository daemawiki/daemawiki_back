package org.daemawiki.domain.user.dto.request;

import org.daemawiki.domain.user.model.UserDetail;

public record EditUserRequest(
        String name,
        UserDetail detail
) {
}
