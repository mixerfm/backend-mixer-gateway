package fm.mixer.gateway.module.react.persistance.entity.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ReactionType {

    LIKE('L'), RECOMMEND('R');

    private final char code;

    public static ReactionType fromCode(char code) {
        return Arrays.stream(values()).filter(type -> type.code == code).findFirst().orElse(null);
    }
}
