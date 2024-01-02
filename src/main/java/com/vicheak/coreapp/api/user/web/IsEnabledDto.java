package com.vicheak.coreapp.api.user.web;

import jakarta.validation.constraints.NotNull;

public record IsEnabledDto(@NotNull(message = "User status must not be null!")
                           Boolean isEnabled) {
}
