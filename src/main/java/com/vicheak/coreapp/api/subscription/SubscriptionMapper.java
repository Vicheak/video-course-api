package com.vicheak.coreapp.api.subscription;

import com.vicheak.coreapp.api.subscription.web.SubscriptionAuthorDto;
import com.vicheak.coreapp.api.subscription.web.SubscriptionDetailDto;
import com.vicheak.coreapp.api.subscription.web.SubscriptionDto;
import com.vicheak.coreapp.api.user.User;
import com.vicheak.coreapp.util.ValueInjectUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class SubscriptionMapper {

    protected ValueInjectUtil valueInjectUtil;

    @Autowired
    public void setValueInjectUtil(ValueInjectUtil valueInjectUtil) {
        this.valueInjectUtil = valueInjectUtil;
    }

    @Mapping(target = "authorUuid", source = "uuid")
    @Mapping(target = "author", source = "username")
    @Mapping(target = "authorEmail", source = "email")
    public abstract SubscriptionAuthorDto fromAuthorToSubscriptionAuthorDto(User author);

    @Mapping(target = "subscriberUuid", source = "uuid")
    @Mapping(target = "subscriber", source = "username")
    @Mapping(target = "subscriberEmail", source = "email")
    @Mapping(target = "subscriberImageUri", expression = "java(valueInjectUtil.getImageUri(subscriber.getPhoto()))")
    public abstract SubscriptionDto fromSubscriberToSubscriptionDto(User subscriber);

    @Mapping(target = "subscriptionDetailId", source = "id")
    @Mapping(target = "courseUuid", source = "course.uuid")
    @Mapping(target = "courseTitle", source = "course.title")
    @Mapping(target = "coursePrice", source = "cost")
    @Mapping(target = "courseDurationInHour", source = "course.durationInHour")
    @Mapping(target = "isApproved", source = "approved")
    public abstract SubscriptionDetailDto fromSubscriptionDetailToSubscriptionDetailDto(SubscriptionDetail subscriptionDetail);

}
