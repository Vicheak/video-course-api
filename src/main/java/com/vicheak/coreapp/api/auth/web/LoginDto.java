package com.vicheak.coreapp.api.auth.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDto(@NotBlank(message = "Email must not be blank!")
                       @Email(message = "Email must be a valid email address!")
                       String email,

                       @NotBlank(message = "Password must not be blank!")
                       @Size(min = 8, message = "Password must be at least 8 characters!")
                       String password) {
}
