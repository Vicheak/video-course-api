package com.vicheak.coreapp.api.subscription;

import com.vicheak.coreapp.api.subscription.web.SubscriptionAuthorDto;
import com.vicheak.coreapp.api.subscription.web.SubscriptionDetailDto;
import com.vicheak.coreapp.api.subscription.web.SubscriptionDto;
import com.vicheak.coreapp.api.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "authorUuid", source = "uuid")
    @Mapping(target = "author", source = "username")
    @Mapping(target = "authorEmail", source = "email")
    SubscriptionAuthorDto fromAuthorToSubscriptionAuthorDto(User author);

    @Mapping(target = "subscriberUuid", source = "uuid")
    @Mapping(target = "subscriber", source = "username")
    @Mapping(target = "subscriberEmail", source = "email")
    SubscriptionDto fromSubscriberToSubscriptionDto(User subscriber);

    @Mapping(target = "subscriptionDetailId", source = "id")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseUuid", source = "course.uuid")
    @Mapping(target = "courseTitle", source = "course.title")
    @Mapping(target = "coursePrice", source = "cost")
    @Mapping(target = "isApproved", source = "approved")
    SubscriptionDetailDto fromSubscriptionDetailToSubscriptionDetailDto(SubscriptionDetail subscriptionDetail);

}
