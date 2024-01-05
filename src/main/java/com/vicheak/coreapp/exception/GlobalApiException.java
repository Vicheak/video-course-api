package com.vicheak.coreapp.exception;

import com.vicheak.coreapp.base.BaseError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalApiException {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException ex) {
        return new ResponseEntity<>(BaseError.builder()
                .isSuccess(true)
                .code(ex.getHttpStatus().value())
                .timestamp(LocalDateTime.now())
                .errors(ex.getReason())
                .build(), ex.getHttpStatus());
    }

}
