package com.example.daemawiki.domain.user.dto.request;

import com.example.daemawiki.domain.user.model.UserDetail;

public record EditUserRequest(
        String name,
        UserDetail detail
) {
}
