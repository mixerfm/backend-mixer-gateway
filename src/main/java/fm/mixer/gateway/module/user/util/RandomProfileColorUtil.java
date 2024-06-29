package fm.mixer.gateway.module.user.util;

import fm.mixer.gateway.error.exception.InternalServerErrorException;
import fm.mixer.gateway.module.user.config.UserProfileColorConfig;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomProfileColorUtil {

    private static UserProfileColorConfig config;

    public RandomProfileColorUtil(UserProfileColorConfig config) {
        RandomProfileColorUtil.config = config;
    }

    public static String randomProfileColor() {
        if (config.getActive().isEmpty()) {
            throw new InternalServerErrorException("No profile color configured");
        }

        return config.getActive().get(new Random().nextInt(config.getActive().size()));
    }
}
