package fm.mixer.gateway.validation.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SpamVariablePath {

    BIOGRAPHY("biography"), CITY("city"), COMMENT("comment"), DISPLAY_NAME("name");

    private final String path;
}
