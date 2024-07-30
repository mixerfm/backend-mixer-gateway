package fm.mixer.gateway.module.notification.scheduler;

import fm.mixer.gateway.module.notification.mapper.NotificationMapper;
import fm.mixer.gateway.module.notification.persistance.repository.UserNotificationRepository;
import fm.mixer.gateway.module.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationMapper mapper;
    private final NotificationService service;
    private final UserNotificationRepository repository;

    @Scheduled(fixedDelay = 60000)
    public void sendNotifications() {
        repository.findAllBySentIsFalse().forEach((notification) -> {
            if (service.sendNotification(mapper.toMessage(notification))) {
                notification.setSent(true);

                repository.save(notification);
            }
        });
    }
}
