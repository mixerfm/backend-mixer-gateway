package fm.mixer.gateway.module.user.persistance.entity.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserGender {

    MALE('M'), FEMALE('F'), OTHER('O');

    private final char code;

    public static UserGender fromCode(char code) {
        return Arrays.stream(values()).filter(v -> v.code == code).findFirst().orElse(null);
    }
}
