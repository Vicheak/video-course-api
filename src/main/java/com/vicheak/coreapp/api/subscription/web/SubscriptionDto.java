package com.vicheak.coreapp.api.subscription.web;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String subscriberImageUri;
    private List<SubscriptionDetailDto> subscriptionDetails;

}
