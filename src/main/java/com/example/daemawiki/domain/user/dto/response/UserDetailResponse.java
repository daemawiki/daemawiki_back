package com.example.daemawiki.domain.user.dto.response;

import com.example.daemawiki.domain.file.model.File;
import com.example.daemawiki.domain.user.model.User;
import lombok.Builder;

@Builder
public record UserDetailResponse(
        String id,
        String name,
        File profile
) {

    public static UserDetailResponse create(User user) {
        return UserDetailResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profile(user.getProfile())
                .build();
    }

}
