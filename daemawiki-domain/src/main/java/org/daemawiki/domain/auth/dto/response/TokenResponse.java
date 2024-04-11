package org.daemawiki.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;

@Builder
public record TokenResponse(
        String token,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "Asia/Seoul")
        LocalDateTime issuedAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "Asia/Seoul")
        LocalDateTime expiredAt
) {

    public static TokenResponse create(Tuple2<String, LocalDateTime> tuple) {
        return TokenResponse.builder()
                .token(tuple.getT1())
                .issuedAt(tuple.getT2())
                .expiredAt(tuple.getT2().plusHours(3))
                .build();
    }

}
