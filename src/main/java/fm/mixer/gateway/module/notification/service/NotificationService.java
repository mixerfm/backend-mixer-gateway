package fm.mixer.gateway.module.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final String DEFAULT_TOPIC = "marketing";

    private final Optional<FirebaseMessaging> messagingClient;

    public boolean sendNotification(Message message) {
        if (messagingClient.isEmpty()) {
            return false;
        }

        try {
            final var response = messagingClient.get().send(message);

            return !response.isEmpty();
        }
        catch (FirebaseMessagingException e) {
            log.error(e.getMessage(), e);

            return false;
        }
    }

    public boolean subscribeToDefaultTopic(String token) {
        return subscribeToTopic(token, DEFAULT_TOPIC);
    }

    public boolean subscribeToTopic(String token, String topic) {
        if (messagingClient.isEmpty()) {
            return false;
        }

        try {
            final var response = messagingClient.get().subscribeToTopic(List.of(token), topic);

            return response.getSuccessCount() == 1 && response.getErrors().isEmpty();
        }
        catch (FirebaseMessagingException e) {
            log.error(String.format("Error subscribing device '%s' to topic '%s'.", token, topic), e);

            return false;
        }
    }
}
