package com.vicheak.coreapp.api.video.web;

import jakarta.validation.constraints.NotBlank;

public record TransactionVideoDto(@NotBlank(message = "Video's title must not be blank!")
                                  String title,

                                  String description,

                                  @NotBlank(message = "Video's link must not be blank!")
                                  String videoLink,

                                  @NotBlank(message = "Video's course uuid must not be blank!")
                                  String courseUuid) {
}
