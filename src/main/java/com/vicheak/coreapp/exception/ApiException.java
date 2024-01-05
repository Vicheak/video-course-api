package com.vicheak.coreapp.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String reason;

}
