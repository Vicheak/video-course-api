package com.vicheak.coreapp.api.video.web;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

public record VideoDto(String uuid,
                       String title,
                       @JsonInclude(JsonInclude.Include.NON_NULL)
                       String description,
                       String videoLink,
                       @JsonInclude(JsonInclude.Include.NON_NULL)
                       String imageUri,
                       LocalDateTime createdAt,
                       LocalDateTime updatedAt,
                       String course,
                       String author) {
}
