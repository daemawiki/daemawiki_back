package com.example.daemawiki.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "이름을 입력해주세요.")
        @Size(max = 5, min = 2, message = "이름은 2자 이상 5자 이하로 설정해야 합니다.")
        String name,
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(dsm\\.hs\\.kr)$",
                message = "메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일을 입력해주세요.")
        String email,
        @NotNull(message = "기수를 입력해주세요.")
        Integer gen,
        @NotBlank(message = "전공을 입력해주세요.")
        String major,
        @NotBlank(message = "동아리를 입력해주세요.")
        String club,
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()-=_+]*$",
                message = "비밀번호 형식이 올바르지 않습니다.")
        String password
) {
}
