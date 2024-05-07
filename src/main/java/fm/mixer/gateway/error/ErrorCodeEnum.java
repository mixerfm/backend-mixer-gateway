package fm.mixer.gateway.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCodeEnum {

    // Server errors
    INTERNAL_SERVER(HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE),

    // Client errors
    REQUEST_METHOD_NOT_SUPPORTED(HttpStatus.METHOD_NOT_ALLOWED),

    MEDIA_TYPE_NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE),

    MEDIA_TYPE_NOT_SUPPORTED(HttpStatus.UNSUPPORTED_MEDIA_TYPE),

    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    METHOD_ARGUMENT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST),
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST),
    METHOD_ARGUMENT_CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST),
    ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST),
    MISSING_REQUEST_HEADER(HttpStatus.BAD_REQUEST),
    MISSING_REQUEST_PATH_VARIABLE(HttpStatus.BAD_REQUEST),

    NO_HANDLER_FOUND(HttpStatus.NOT_FOUND),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),

    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS);

    private final HttpStatus status;
}