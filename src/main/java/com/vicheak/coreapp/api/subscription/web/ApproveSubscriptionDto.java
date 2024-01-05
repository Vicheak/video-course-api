package com.vicheak.coreapp.api.subscription.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ApproveSubscriptionDto(@NotNull(message = "Subscription detail must not be null!")
                                     @Positive(message = "Subscription detail must be positive!")
                                     Long subscriptionDetailId,

                                     @NotNull(message = "Course must not be null!")
                                     @Positive(message = "Course must be positive!")
                                     Long courseId,

                                     @NotNull(message = "Approve must not be null! true means approved and false means reject!")
                                     Boolean approve) {
}
