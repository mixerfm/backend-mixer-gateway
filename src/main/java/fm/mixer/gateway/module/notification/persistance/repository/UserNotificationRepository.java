package fm.mixer.gateway.module.notification.persistance.repository;

import fm.mixer.gateway.module.notification.persistance.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    List<UserNotification> findAllBySentIsFalse();
}
