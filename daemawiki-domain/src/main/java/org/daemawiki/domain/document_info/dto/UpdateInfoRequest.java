package org.daemawiki.domain.document_info.dto;

import jakarta.validation.constraints.NotNull;
import org.daemawiki.domain.document_info.model.Detail;

import java.util.List;

public record UpdateInfoRequest(
        @NotNull(message = "subTitle이 null입니다.")
        String subTitle,
        List<Detail> details,
        @NotNull(message = "문서의 버전을 입력해주세요.")
        Long version
) {
}
