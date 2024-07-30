package fm.mixer.gateway.module.notification.scheduler;

import fm.mixer.gateway.module.auth.persistance.repository.UserDeviceRepository;
import fm.mixer.gateway.module.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserDeviceScheduler {

    private final NotificationService service;
    private final UserDeviceRepository repository;

    @Scheduled(fixedDelay = 60000)
    public void registerUserDevice() {
        repository.findAllByRegisteredIsFalseAndTokenIsNotNull().forEach(userDevice -> {
            if (service.subscribeToDefaultTopic(userDevice.getToken())) {
                userDevice.setRegistered(true);

                repository.save(userDevice);
            }
        });
    }
}
