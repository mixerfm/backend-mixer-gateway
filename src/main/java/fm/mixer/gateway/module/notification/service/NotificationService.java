package fm.mixer.gateway.module.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
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

    public void sendNotification(Message message) {
        messagingClient.ifPresent(client -> {
            try {
                client.send(message);
            }
            catch (FirebaseMessagingException e) {
                log.error(e.getMessage(), e);

                throw new ExternalServiceException();
            }
        });
    }

    public void subscribeToDefaultTopic(String token) {
        subscribeToTopic(token, DEFAULT_TOPIC);
    }

    public void subscribeToTopic(String token, String topic) {
        messagingClient.ifPresent(client -> {
            try {
                client.subscribeToTopic(List.of(token), topic);
            }
            catch (FirebaseMessagingException e) {
                log.error(String.format("Error subscribing device '%s' to topic '%s'.", token, topic), e);
            }
        });
    }
}
