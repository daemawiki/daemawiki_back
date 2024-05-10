package org.daemawiki.domain.user.dto.response;

import org.daemawiki.domain.file.model.File;
import org.daemawiki.domain.user.model.User;

public record UserDetailVo(
        String id,
        String name,
        File profile
) {

    public static UserDetailVo create(User user) {
        return new UserDetailVo(user.getId(), user.getName(), user.getProfile());
    }

}
