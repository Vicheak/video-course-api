package com.vicheak.coreapp.api.subscription;

import com.vicheak.coreapp.api.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByAuthor(User author);

    List<Subscription> findBySubscriberAndAuthor(User subscriber, User author);

}
