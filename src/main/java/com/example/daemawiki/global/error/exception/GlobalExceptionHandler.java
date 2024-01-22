package com.example.daemawiki.global.error.exception;

import com.example.daemawiki.global.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        int status = HttpStatus.BAD_REQUEST.value();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "Bad Request";
        ErrorResponse errorResponse = ErrorResponse.of(status, message);

        return Mono.just(ResponseEntity
                .status(status)
                .body(errorResponse));
    }

}