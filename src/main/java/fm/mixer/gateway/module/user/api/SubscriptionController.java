package fm.mixer.gateway.module.user.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
public class SubscriptionController {

    @PostMapping("/update-subscription")
    public ResponseEntity<Void> updateSubscription(@RequestBody String subscription) {
        log.info("SK_TEMP => {}", subscription);

        return ResponseEntity.created(URI.create("/users/username")).build();
    }
}
