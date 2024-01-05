package com.vicheak.coreapp.api.video.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransactionVideoDto(@NotBlank(message = "Video's title must not be blank!")
                                  String title,

                                  String description,

                                  @NotBlank(message = "Video's link must not be blank!")
                                  String videoLink,

                                  @NotNull(message = "Video's course must not be null!")
                                  @Positive(message = "Video's course ID must be positive!")
                                  Long courseId) {
}
