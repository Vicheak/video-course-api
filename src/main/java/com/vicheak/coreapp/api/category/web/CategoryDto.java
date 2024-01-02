package com.vicheak.coreapp.api.category.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryDto(@NotBlank(message = "Category's name must not be blank!")
                          @Size(max = 100, message = "Category's name must be less than or equal to 100 characters!")
                          String name) {
}
