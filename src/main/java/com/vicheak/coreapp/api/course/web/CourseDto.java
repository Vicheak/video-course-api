package com.vicheak.coreapp.api.course.web;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CourseDto(String uuid,
                        String title,
                        @JsonInclude(value = JsonInclude.Include.NON_NULL)
                        String description,
                        @JsonInclude(value = JsonInclude.Include.NON_NULL)
                        String imageUri,
                        @JsonInclude(value = JsonInclude.Include.NON_NULL)
                        Integer durationInHour,
                        @JsonInclude(value = JsonInclude.Include.NON_NULL)
                        BigDecimal cost,
                        Long numberOfView,
                        Long numberOfLike,
                        String category,
                        String author,
                        LocalDateTime createdAt,
                        LocalDateTime updatedAt) {
}
