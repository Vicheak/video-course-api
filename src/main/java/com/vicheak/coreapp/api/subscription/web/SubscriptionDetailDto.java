package com.vicheak.coreapp.api.subscription.web;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SubscriptionDetailDto(Long subscriptionDetailId,
                                    Long courseId,
                                    String courseUuid,
                                    String courseTitle,
                                    BigDecimal coursePrice,
                                    Boolean isApproved) {
}
