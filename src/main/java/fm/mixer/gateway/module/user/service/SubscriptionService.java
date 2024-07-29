package fm.mixer.gateway.module.user.service;

import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.module.user.model.AdaptyEvent;
import fm.mixer.gateway.module.user.persistance.entity.model.UserSubscriptionType;
import fm.mixer.gateway.module.user.persistance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserRepository repository;

    public void updateUserSubscription(String customerId, AdaptyEvent event) {
        final var user = repository.findByIdentifierAndActiveIsTrue(customerId).orElseThrow(ResourceNotFoundException::new);

        switch (event) {
            case SUBSCRIPTION_STARTED -> user.setType(UserSubscriptionType.PREMIUM);
            case SUBSCRIPTION_END -> user.setType(UserSubscriptionType.FREE);
        }

        repository.save(user);
    }
}
