package fm.mixer.gateway.error.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TooManyRequestsException extends RuntimeException {

    private final String fallbackFactoryName;
}
