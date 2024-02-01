package com.example.daemawiki.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @Size(max = 10, min = 1, message = "닉네임은 1자 이상 10자 이하로 설정해야 합니다.")
        String nickname,
        @NotBlank(message = "이메일을 입력해주세요.")
        String email,
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()-=_+]*$",
                message = "비밀번호 형식이 올바르지 않습니다.")
        String password
) {
}
