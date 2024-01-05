package com.vicheak.coreapp.api.subscription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionDetailRepository extends JpaRepository<SubscriptionDetail, Long> {

    Optional<SubscriptionDetail> findByIdAndCourseId(Long subscriptionDetailId, Long courseId);

    List<SubscriptionDetail> findBySubscription(Subscription subscription);

}
