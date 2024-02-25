package com.example.daemawiki.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateClubRequest(
        @NotBlank(message = "추가할 동아리를 입력해주세요.")
        String club
) {
}
