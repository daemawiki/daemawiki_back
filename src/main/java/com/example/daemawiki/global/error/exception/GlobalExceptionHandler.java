package com.example.daemawiki.global.error.exception;

import com.example.daemawiki.global.error.ErrorResponse;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGlobalException(CustomException e) {
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode().getHttpStatus(), e.getErrorCode().getMessage());

        return Mono.just(ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(errorResponse));
    }

    private final int badRequestStatus = HttpStatus.BAD_REQUEST.value();

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidException(WebExchangeBindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        int status = badRequestStatus;
        String message = fieldError != null ? fieldError.getDefaultMessage() : "Bad Request";
        ErrorResponse errorResponse = ErrorResponse.of(status, message);

        return Mono.just(ResponseEntity
                .status(status)
                .body(errorResponse));
    }

    @ExceptionHandler(DataBufferLimitException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleLimitException(DataBufferLimitException e) {
        int status = HttpStatus.EXPECTATION_FAILED.value();
        return Mono.just(ResponseEntity
                .status(status)
                .body(ErrorResponse.of(status, "파일 최대 크기인 5mb를 초과하였습니다.")));
    }

}