package fm.mixer.gateway.common.util;

import fm.mixer.gateway.auth.exception.AccessForbiddenException;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.mix.persistance.entity.model.VisibilityType;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.entity.model.UserSubscriptionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckUserPermissionUtil {

    public static void checkUserPermission(User user, Mix mix) {
        // User is the author of the mix - all ok
        if (user.getId().equals(mix.getUser().getId())) {
            return;
        }

        // User is not premium but mix is premium
        if (!UserSubscriptionType.PREMIUM.equals(user.getType()) && VisibilityType.PREMIUM.equals(mix.getVisibility())) {
            throw new AccessForbiddenException();
        }

        // Mix is private, but user is not the author of it
        if (VisibilityType.PRIVATE.equals(mix.getVisibility())) {
            throw new AccessForbiddenException();
        }
    }
}
