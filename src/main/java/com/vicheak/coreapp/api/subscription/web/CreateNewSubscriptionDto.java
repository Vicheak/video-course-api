package com.vicheak.coreapp.api.subscription.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateNewSubscriptionDto(@NotBlank(message = "Author UUID must not be blank!")
                                       String authorUuid,

                                       @NotNull(message = "Course must not be null!")
                                       @Size(min = 1, message = "There must be at least a course!")
                                       Set<@NotNull(message = "Course must not be null!")
                                       @Positive(message = "Course must be positive!") Long> courseIds) {
}
