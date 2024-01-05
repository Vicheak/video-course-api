package com.vicheak.coreapp.api.auth.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyDto(@NotBlank(message = "Email should not be blank!")
                        @Size(max = 150, message = "Email should not be more than 150 characters!")
                        @Email(message = "Email should be in well-formed email address!")
                        String email,

                        @NotBlank(message = "Verified code should not be blank!")
                        @Size(min = 6, message = "Verified code must be a six-digit code!")
                        String verifiedCode) {
}
