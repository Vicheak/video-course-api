package com.vicheak.coreapp.api.subscription.web;

import com.vicheak.coreapp.api.subscription.SubscriptionService;
import com.vicheak.coreapp.base.BaseApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/{uuid}")
    public BaseApi<?> loadSubscriptionByAuthorUuid(@PathVariable String uuid) {

        SubscriptionAuthorDto subscriptionAuthorDto =
                subscriptionService.loadSubscriptionByAuthorUuid(uuid);

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("Subscription request loaded successfully!")
                .timestamp(LocalDateTime.now())
                .payload(subscriptionAuthorDto)
                .build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createNewSubscription(@RequestBody @Valid CreateNewSubscriptionDto createNewSubscriptionDto) {
        subscriptionService.createNewSubscription(createNewSubscriptionDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public void approveOrRejectSubscription(@RequestBody @Valid ApproveSubscriptionDto approveSubscriptionDto){
        subscriptionService.approveOrRejectSubscription(approveSubscriptionDto);
    }

}
