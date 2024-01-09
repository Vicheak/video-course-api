package com.vicheak.coreapp.api.course.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LikeDto(@NotBlank(message = "Course UUID must not be blank!")
                      String courseUuid,

                      @NotNull(message = "Like status must not be null!")
                      Boolean isLike) {
}
