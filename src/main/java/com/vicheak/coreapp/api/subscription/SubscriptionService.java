package com.vicheak.coreapp.api.subscription;

import com.vicheak.coreapp.api.subscription.web.ApproveSubscriptionDto;
import com.vicheak.coreapp.api.subscription.web.CreateNewSubscriptionDto;
import com.vicheak.coreapp.api.subscription.web.SubscriptionAuthorDto;

public interface SubscriptionService {

    /**
     * This method is used to load subscription response dto by author uuid
     * @param uuid is the path parameter from client
     * @return SubscriptionAuthorDto
     */
    SubscriptionAuthorDto loadSubscriptionByAuthorUuid(String uuid);

    /**
     * This method is used to create new subscription when user enrolls any courses
     * @param createNewSubscriptionDto is the request from client
     */
    void createNewSubscription(CreateNewSubscriptionDto createNewSubscriptionDto);

    /**
     * This method is used to approve or reject the subscription from author dashboard
     * @param approveSubscriptionDto is the request from client
     */
    void approveOrRejectSubscription(ApproveSubscriptionDto approveSubscriptionDto);

}
