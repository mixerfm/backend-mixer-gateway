package fm.mixer.gateway.module.user.persistance.entity.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserSubscriptionType {

    FREE((short) 0), PREMIUM((short) 1);

    private final short code;

    public static UserSubscriptionType fromCode(short code) {
        return Arrays.stream(values()).filter(v -> v.code == code).findFirst().orElse(null);
    }
}
