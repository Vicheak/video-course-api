package com.vicheak.coreapp.api.auth.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record VerificationCodeDto(@NotBlank(message = "Email should not be blank!")
                                  @Size(max = 150, message = "Email should not be more than 150 characters!")
                                  @Email(message = "Email should be in well-formed email address!")
                                  String email) {
}
