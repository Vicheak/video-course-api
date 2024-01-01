package com.vicheak.coreapp.base;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BaseError<T>(Boolean isSuccess,
                           Integer code,
                           String message,
                           LocalDateTime timestamp,
                           T errors) {
}
