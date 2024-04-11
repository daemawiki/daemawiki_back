package org.daemawiki.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private int status;
    private String message;
    private String detail;

    public static ErrorResponse of(int status, String message, String detail) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .detail(detail)
                .build();
    }

}