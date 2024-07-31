package fm.mixer.gateway.module.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import fm.mixer.gateway.error.exception.ExternalServiceException;
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
            log.error("Send Notification - No firebase messaging client available");

            return false;
        }

        try {
            final var response = messagingClient.get().send(message);

            if (response.isEmpty()) {
                throw new ExternalServiceException("Failed to send message");
            }

            return true;
        }
        catch (Exception e) {
            log.error("Send Notification - Error while sending, reason: {}", e.getMessage(), e);

            return false;
        }
    }

    public boolean subscribeToDefaultTopic(String token) {
        return subscribeToTopic(token, DEFAULT_TOPIC);
    }

    public boolean subscribeToTopic(String token, String topic) {
        if (messagingClient.isEmpty()) {
            log.error("Topic subscription - No firebase messaging client available");

            return false;
        }

        try {
            final var response = messagingClient.get().subscribeToTopic(List.of(token), topic);

            if (response.getSuccessCount() != 1 || !response.getErrors().isEmpty()) {
                throw new ExternalServiceException(String.format("Success count is %d and list of errors are: %s", response.getSuccessCount(), response.getErrors()));
            }

            return true;
        }
        catch (Exception e) {
            log.error(String.format("Error subscribing device '%s' to topic '%s', reason: %s", token, topic, e.getMessage()), e);

            return false;
        }
    }
}
