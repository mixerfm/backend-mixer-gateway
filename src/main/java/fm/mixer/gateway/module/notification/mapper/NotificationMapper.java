package fm.mixer.gateway.module.notification.mapper;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import fm.mixer.gateway.module.notification.persistance.entity.UserNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NotificationMapper {

    Notification toNotification(UserNotification userNotification);

    @Mapping(target = "notification", source = ".")
    Message toMessage(UserNotification userNotification);
}
