package com.vicheak.coreapp.api.subscription;

import com.vicheak.coreapp.api.subscription.web.ApproveSubscriptionDto;
import com.vicheak.coreapp.api.subscription.web.CreateNewSubscriptionDto;
import com.vicheak.coreapp.api.subscription.web.SubscriptionAuthorDto;
import com.vicheak.coreapp.api.subscription.web.SubscriptionDto;
import org.springframework.security.core.Authentication;

public interface SubscriptionService {

    /**
     * This method is used to load subscription report by authenticated author
     *
     * @param authentication is the request from client
     * @return SubscriptionAuthorDto
     */
    SubscriptionAuthorDto loadSubscriptionByAuthenticatedAuthor(Authentication authentication);

    /**
     * This method is used to load subscription report by authenticated subscriber
     * @param authentication is the request from client
     * @return SubscriptionDto
     */
    SubscriptionDto loadSubscriptionByAuthenticatedSubscriber(Authentication authentication);

    /**
     * This method is used to create new subscription when user enrolls any courses
     *
     * @param createNewSubscriptionDto is the request from client
     */
    void createNewSubscription(CreateNewSubscriptionDto createNewSubscriptionDto);

    /**
     * This method is used to approve or reject the subscription from author dashboard
     *
     * @param approveSubscriptionDto is the request from client
     */
    void approveOrRejectSubscription(ApproveSubscriptionDto approveSubscriptionDto);

    /**
     * This method is used to remove subscription detail by its id
     * @param subscriptionDetailId is the path parameter from client
     */
    void removeSubscriptionDetailById(Long subscriptionDetailId);

}
