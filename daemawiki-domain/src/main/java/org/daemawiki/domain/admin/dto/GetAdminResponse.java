package org.daemawiki.domain.admin.dto;

import org.daemawiki.domain.admin.model.Admin;

public record GetAdminResponse(
        String email,
        String userId
) {

    public static GetAdminResponse of(Admin admin) {
        return new GetAdminResponse(
                admin.getEmail(),
                admin.getUserId()
        );
    }

}
