package com.vicheak.coreapp.api.subscription;

import com.vicheak.coreapp.api.course.Course;
import com.vicheak.coreapp.api.course.CourseRepository;
import com.vicheak.coreapp.api.course.CourseServiceImpl;
import com.vicheak.coreapp.api.subscription.web.*;
import com.vicheak.coreapp.api.user.User;
import com.vicheak.coreapp.api.user.UserRepository;
import com.vicheak.coreapp.api.user.UserRole;
import com.vicheak.coreapp.exception.ApiException;
import com.vicheak.coreapp.security.CustomUserDetails;
import com.vicheak.coreapp.security.SecurityContextHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final SubscriptionDetailRepository subscriptionDetailRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final SecurityContextHelper securityContextHelper;

    @Override
    public SubscriptionAuthorDto loadSubscriptionByAuthenticatedAuthor() {
        //load author resource from authenticated user
        User author = securityContextHelper.loadAuthenticatedUser();

        //load subscriptions by the existing author
        List<Subscription> subscriptions = subscriptionRepository.findByAuthor(author);

        //check if the author has no subscription request
        if (subscriptions.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND,
                    "Author has no subscription request!");

        //map from author to build basic subscription author dto information
        SubscriptionAuthorDto subscriptionAuthorDto =
                subscriptionMapper.fromAuthorToSubscriptionAuthorDto(author);

        //this code is to build list of subscription dto
        List<SubscriptionDto> subscriptionDtoList = new ArrayList<>();

        //group by subscriber
        Map<User, List<Subscription>> subscriptionAuthorMap = subscriptions.stream()
                .collect(Collectors.groupingBy(Subscription::getSubscriber));

        for (Map.Entry<User, List<Subscription>> entry : subscriptionAuthorMap.entrySet()) {
            User subscriber = entry.getKey();
            List<Subscription> subscriptionSubscribers = entry.getValue();

            //map from subscriber to build basic subscription detail dto information
            SubscriptionDto subscriptionDto =
                    subscriptionMapper.fromSubscriberToSubscriptionDto(subscriber);

            //this code is to build list of subscription detail dto
            List<SubscriptionDetailDto> subscriptionDetailDtoList = new ArrayList<>();

            subscriptionSubscribers.forEach(subscriptionSubscriber -> {
                //load list of subscription details via subscription id
                List<SubscriptionDetail> subscriptionDetails =
                        subscriptionDetailRepository.findBySubscription(subscriptionSubscriber);

                subscriptionDetails.forEach(subscriptionDetail -> {
                    //map from subscription detail to build basic subscription detail dto
                    subscriptionDetailDtoList.add(subscriptionMapper.fromSubscriptionDetailToSubscriptionDetailDto(subscriptionDetail));
                });
            });

            subscriptionDto.setSubscriptionDetails(subscriptionDetailDtoList);

            subscriptionDtoList.add(subscriptionDto);
        }

        subscriptionAuthorDto.setSubscriptions(subscriptionDtoList);

        return subscriptionAuthorDto;
    }

    @Override
    public SubscriptionDto loadSubscriptionByAuthenticatedSubscriber() {
        //load subscriber resource from authenticated user
        User subscriber = securityContextHelper.loadAuthenticatedUser();

        //load subscriptions by the existing subscriber
        List<Subscription> subscriptions = subscriptionRepository.findBySubscriber(subscriber);

        //check if the subscriber has no subscription request
        if (subscriptions.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND,
                    "You have no subscription request!");

        //map from subscriber to build basic subscription detail dto information
        SubscriptionDto subscriptionDto =
                subscriptionMapper.fromSubscriberToSubscriptionDto(subscriber);

        //this code is to build list of subscription detail dto
        List<SubscriptionDetailDto> subscriptionDetailDtoList = new ArrayList<>();

        subscriptions.forEach(subscription -> {
            List<SubscriptionDetail> subscriptionDetails = subscription.getSubscriptionDetails();

            subscriptionDetails.forEach(subscriptionDetail -> {
                //map from subscription detail to build basic subscription detail dto
                subscriptionDetailDtoList.add(subscriptionMapper.fromSubscriptionDetailToSubscriptionDetailDto(subscriptionDetail));
            });
        });

        subscriptionDto.setSubscriptionDetails(subscriptionDetailDtoList);

        return subscriptionDto;
    }

    @Transactional
    @Override
    public void createNewSubscription(CreateNewSubscriptionDto createNewSubscriptionDto) {
        //load authenticated user
        User subscriber = securityContextHelper.loadAuthenticatedUser();

        //check author uuid if exists
        User author = userRepository.findByUuid(createNewSubscriptionDto.authorUuid())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Author with uuid, %s has not been found in the system!"
                                        .formatted(createNewSubscriptionDto.authorUuid()))
                );

        //check if the user is author
        if (!checkIfUserIsAuthor(author))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You must subscribe to the author!");

        if (subscriber.getUuid().equals(author.getUuid()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Subscriber must subscribe to another author!");

        //check if the author contains any courses
        if (!courseRepository.existsByUser(author))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "This author contains no course!");

        //check courses if all exist
        checkCourseIfExist(createNewSubscriptionDto.courseIds());

        //check if the author contains the specific courses
        checkIfAuthorContainsSpecificCourses(createNewSubscriptionDto.courseIds(), author);

        //check if the subscriber already subscribes to the courses from the same author
        checkIfSubscriberAlreadyEnrolls(subscriber, author, createNewSubscriptionDto.courseIds());

        Subscription newSubscription = new Subscription();
        newSubscription.setSubscriber(subscriber);
        newSubscription.setAuthor(author);

        //save the new subscription into the database
        subscriptionRepository.save(newSubscription);

        //set up subscription details
        List<SubscriptionDetail> subscriptionDetails = new ArrayList<>();

        createNewSubscriptionDto.courseIds().forEach(courseId -> {
            Course subscribedCourse = courseRepository.findById(courseId)
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Course has not been found in the system!")
                    );

            subscriptionDetails.add(SubscriptionDetail.builder()
                    .course(subscribedCourse)
                    .cost(subscribedCourse.getCost())
                    .approved(false)
                    .subscription(newSubscription)
                    .build());
        });

        //save subscription details into the database
        subscriptionDetailRepository.saveAll(subscriptionDetails);
    }

    @Transactional
    @Override
    public void approveOrRejectSubscription(ApproveSubscriptionDto approveSubscriptionDto) {
        //load subscription detail exists with the course
        SubscriptionDetail subscriptionDetail = subscriptionDetailRepository.findByIdAndCourseId(
                        approveSubscriptionDto.subscriptionDetailId(), approveSubscriptionDto.courseId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Subscription detail does not exist with the course!")
                );

        //check the subscription detail if it belongs to the authenticated author
        User authenticatedAuthor = securityContextHelper.loadAuthenticatedUser();
        checkSubscriptionAuthor(subscriptionDetail, authenticatedAuthor);

        subscriptionDetail.setApproved(approveSubscriptionDto.approve());

        subscriptionDetailRepository.save(subscriptionDetail);
    }

    @Transactional
    @Override
    public void removeSubscriptionDetailById(Long subscriptionDetailId) {
        //load subscription detail resource by id
        SubscriptionDetail subscriptionDetail = subscriptionDetailRepository.findById(subscriptionDetailId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Subscription detail ID has not been found in the system!")
                );

        //check the subscription detail if it belongs to the authenticated author
        User authenticatedAuthor = securityContextHelper.loadAuthenticatedUser();
        checkSubscriptionAuthor(subscriptionDetail, authenticatedAuthor);

        subscriptionDetailRepository.delete(subscriptionDetail);
    }

    private boolean checkIfUserIsAuthor(User user) {
        List<UserRole> userRoles = user.getUserRoles();
        for (UserRole userRole : userRoles)
            if (userRole.getRole().getName().equals("AUTHOR")) return true;
        return false;
    }

    private void checkCourseIfExist(Set<Long> courseIds) {
        boolean allExisted = courseIds.stream()
                .allMatch(courseRepository::existsById);

        if (!allExisted)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Courses are not valid in the system! please check!");
    }

    private void checkIfAuthorContainsSpecificCourses(Set<Long> courseIds, User author) {
        courseIds.forEach(courseId -> {
            if (!courseRepository.existsByIdAndUser(courseId, author))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "The author contains no course with id, %d!"
                                .formatted(courseId));
        });
    }

    private void checkIfSubscriberAlreadyEnrolls(User subscriber, User author, Set<Long> courseIds) {
        List<Subscription> subscriptions =
                subscriptionRepository.findBySubscriberAndAuthor(subscriber, author);

        if (!subscriptions.isEmpty()) {
            subscriptions.forEach(subscription -> {
                List<SubscriptionDetail> subscriptionDetails =
                        subscription.getSubscriptionDetails();

                subscriptionDetails.forEach(subscriptionDetail -> {
                    if (courseIds.contains(subscriptionDetail.getCourse().getId()))
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                "You have already subscribed to the course! please check!");
                });
            });
        }
    }

    private void checkSubscriptionAuthor(SubscriptionDetail subscriptionDetail, User authenticatedAuthor) {
        Subscription subscription = subscriptionDetail.getSubscription();
        User author = subscription.getAuthor();
        if (!authenticatedAuthor.getUuid().equals(author.getUuid()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Permission denied!");
    }

}
