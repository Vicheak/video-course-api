package com.vicheak.coreapp.api.user.web;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

public record TransactionUserDto(@NotBlank(message = "Username must not be blank!")
                            String username,

                                 @NotBlank(message = "Email must not be blank!")
                            @Email(message = "Email must be a valid email address!")
                            String email,

                                 @NotBlank(message = "Password must not be blank!")
                            @Size(min = 8, message = "Password must be at least 8 characters!")
                            String password,

                                 @NotBlank(message = "Gender must not be blank!")
                            String gender,

                                 @NotBlank(message = "Phone Number must not be blank!")
                            String phoneNumber,

                                 @NotNull(message = "Date of Birth must not be null!")
                            @Past(message = "Date of Birth must be in the past!")
                            LocalDate dateOfBirth,

                                 @NotNull(message = "Role must not be null!")
                            @Size(min = 1, message = "User must have at least one role!")
                            Set<@NotNull(message = "Role must not be null!")
                            @Positive(message = "Role must be positive!") Integer> roleIds) {
}
