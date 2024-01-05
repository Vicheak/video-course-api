package com.vicheak.coreapp.api.subscription.web;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionAuthorDto {

    private String authorUuid;
    private String author;
    private String authorEmail;
    private List<SubscriptionDto> subscriptions;

}
