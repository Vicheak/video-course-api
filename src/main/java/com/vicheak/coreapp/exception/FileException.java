package com.vicheak.coreapp.exception;

import com.vicheak.coreapp.base.BaseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class FileException {

    @ExceptionHandler({MultipartException.class, MaxUploadSizeExceededException.class})
    public ResponseEntity<?> handleFileException(Exception ex) {
        var baseError = BaseError.builder()
                .isSuccess(false)
                .message("Something went wrong, please check...!")
                .code(3000)
                .timestamp(LocalDateTime.now())
                .errors(ex.getMessage())
                .build();

        return new ResponseEntity<>(baseError,
                ex.getClass().getSimpleName().equals("MaxUploadSizeExceededException") ?
                        HttpStatus.PAYLOAD_TOO_LARGE : HttpStatus.BAD_REQUEST);
    }

}
