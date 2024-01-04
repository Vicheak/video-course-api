package com.vicheak.coreapp.exception;

import com.vicheak.coreapp.base.BaseError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class FormatException {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NumberFormatException.class, DateTimeParseException.class})
    public BaseError<?> handleFormatException() {
        return BaseError.builder()
                .isSuccess(false)
                .message("Something went wrong, please check...!")
                .code(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .errors("There is such invalid format!")
                .build();
    }

}
