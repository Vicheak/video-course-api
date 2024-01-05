package com.vicheak.coreapp.api.subscription.web;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDto {

    private String subscriberUuid;
    private String subscriber;
    private String subscriberEmail;
    private List<SubscriptionDetailDto> subscriptionDetails;

}
