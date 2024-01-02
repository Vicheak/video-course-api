package com.vicheak.coreapp.api.user.web;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UserDto(String uuid,
                      String username,
                      String email,
                      String gender,
                      String phoneNumber,
                      LocalDate dateOfBirth,
                      @JsonInclude(JsonInclude.Include.NON_NULL)
                      String photoUri,
                      LocalDateTime joinDate,
                      Boolean isVerified,
                      Boolean isEnabled,
                      List<UserRoleDto> userRoles) {
}
