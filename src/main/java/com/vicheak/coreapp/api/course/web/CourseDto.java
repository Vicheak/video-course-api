package com.vicheak.coreapp.api.course.web;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

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
                        String category) {
}
