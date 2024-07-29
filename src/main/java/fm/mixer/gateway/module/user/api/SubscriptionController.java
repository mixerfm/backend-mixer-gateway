package fm.mixer.gateway.module.user.api;

import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.module.user.model.AdaptyCheckResponse;
import fm.mixer.gateway.module.user.model.AdaptyRequest;
import fm.mixer.gateway.module.user.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService service;

    @PostMapping(value = "/update-subscription", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AdaptyCheckResponse> updateSubscription(@RequestBody AdaptyRequest adaptyRequest) {
        // This is a check-request, so we must return received identifier
        if (StringUtils.hasText(adaptyRequest.getIdentifier())) {
            return ResponseEntity.ok(new AdaptyCheckResponse(adaptyRequest.getIdentifier()));
        }
        // Validate request
        if (!StringUtils.hasText(adaptyRequest.getCustomerId()) || Objects.isNull(adaptyRequest.getEvent())) {
            throw new BadRequestException("customer.and.event.are.required.error");
        }

        service.updateUserSubscription(adaptyRequest.getCustomerId(), adaptyRequest.getEvent());

        return ResponseEntity.created(URI.create("/users/" + adaptyRequest.getCustomerId())).build();
    }
}
