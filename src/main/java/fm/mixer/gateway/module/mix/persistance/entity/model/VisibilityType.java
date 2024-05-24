package fm.mixer.gateway.module.mix.persistance.entity.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum VisibilityType {

    PUBLIC((short) 0), PRIVATE((short) 1), PREMIUM((short) 2);

    private final short code;

    public static VisibilityType fromCode(short code) {
        return Arrays.stream(values()).filter(v -> v.code == code).findFirst().orElse(null);
    }
}
