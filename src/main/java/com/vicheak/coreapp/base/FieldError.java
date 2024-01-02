package com.vicheak.coreapp.base;

import lombok.Builder;

@Builder
public record FieldError(String fieldName,
                         String message) {
}
