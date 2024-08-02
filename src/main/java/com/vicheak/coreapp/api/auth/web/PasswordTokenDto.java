package com.vicheak.coreapp.api.auth.web;

import lombok.Builder;

@Builder
public record PasswordTokenDto(String message,
                               String token) {
}
