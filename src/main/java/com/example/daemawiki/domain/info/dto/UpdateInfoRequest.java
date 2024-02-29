package com.example.daemawiki.domain.info.dto;

import com.example.daemawiki.domain.info.model.Info;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateInfoRequest(
        @NotBlank(message = "문서의 id를 입력해주세요.")
        String documentId,
        List<Info> infoList,
        @NotNull(message = "문서의 버전을 입력해주세요.")
        Integer version
) {
}
