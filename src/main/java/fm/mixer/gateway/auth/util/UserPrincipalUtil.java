package fm.mixer.gateway.auth.util;

import fm.mixer.gateway.auth.service.AuthUserService;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserPrincipalUtil {

    private static AuthUserService authUserService;

    public UserPrincipalUtil(AuthUserService authUserService) {
        UserPrincipalUtil.authUserService = authUserService;
    }

    public static Optional<User> getCurrentActiveUser() {
        return authUserService.getCurrentActiveUser();
    }

    public static Optional<String> resolveUserEmailFromJwt() {
        return authUserService.resolveUserEmailFromJwt();
    }
}
