package org.daemawiki.domain.auth.dto.response;

import lombok.Builder;

@Builder
public record TokenResponse(
        String token
) {

    public static TokenResponse create(String token) {
        return TokenResponse.builder().token(token).build();
    }

}
