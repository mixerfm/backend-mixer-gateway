package fm.mixer.gateway.module.user.api;

import fm.mixer.gateway.module.user.api.v1.MarketingApiDelegate;
import fm.mixer.gateway.module.user.api.v1.model.SubscribeRequest;
import fm.mixer.gateway.module.user.api.v1.model.UnsubscribeRequest;
import fm.mixer.gateway.module.user.service.NewsletterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class MarketingController implements MarketingApiDelegate {

    private final NewsletterService service;

    @Override
    public ResponseEntity<Void> subscribe(SubscribeRequest subscribeRequest) {
        final var newsletter = service.subscribe(subscribeRequest.getEmail());

        return ResponseEntity.created(URI.create("/newsletters/subscription/" + newsletter.getIdentifier())).build();
    }

    @Override
    public ResponseEntity<Void> unsubscribe(UnsubscribeRequest unsubscribeRequest) {
        service.unsubscribe(unsubscribeRequest.getToken(), unsubscribeRequest.getEmail());

        return ResponseEntity.noContent().build();
    }
}
