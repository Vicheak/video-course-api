package com.vicheak.coreapp.api.subscription.web;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SubscriptionDetailDto(Long subscriptionDetailId,
                                    String courseUuid,
                                    String courseTitle,
                                    BigDecimal coursePrice,
                                    Integer courseDurationInHour,
                                    Boolean isApproved) {
}
