package com.vicheak.coreapp.spec;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record VideoFilter(String title,
                          LocalDate fromDate,
                          LocalDate toDate) {
}
