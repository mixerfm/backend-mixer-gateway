package fm.mixer.gateway.error.mapper;

import fm.mixer.gateway.model.ErrorType;
import fm.mixer.gateway.test.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class ErrorMapperUnitTest {

    final ErrorMapper mapper = Mappers.getMapper(ErrorMapper.class);

    @MethodSource("provideErrorTypes")
    @ParameterizedTest
    void shouldMapToHttpStatus(ErrorType given, HttpStatus expected) {
        // When
        final var result = mapper.toHttpStatus(given);

        // Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldMapToError() {
        // Given
        final var errorMessage = "test message";
        final var type = ErrorType.BAD_REQUEST;

        // When
        final var result = mapper.toError(type, errorMessage);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(type);
        assertThat(result.getIdentifier()).isNotNull();
        assertThat(result.getDescription()).isEqualTo(errorMessage);
    }

    private static Stream<Arguments> provideErrorTypes() {
        return Stream.of(
            Arguments.of(ErrorType.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
            Arguments.of(ErrorType.EXTERNAL_SERVICE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
            Arguments.of(ErrorType.EXTERNAL_SERVICE_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE),
            Arguments.of(ErrorType.REQUEST_METHOD_NOT_SUPPORTED, HttpStatus.METHOD_NOT_ALLOWED),
            Arguments.of(ErrorType.MEDIA_TYPE_NOT_ACCEPTABLE, HttpStatus.NOT_ACCEPTABLE),
            Arguments.of(ErrorType.MEDIA_TYPE_NOT_SUPPORTED, HttpStatus.UNSUPPORTED_MEDIA_TYPE),
            Arguments.of(ErrorType.TOO_MANY_REQUESTS, HttpStatus.TOO_MANY_REQUESTS),
            Arguments.of(ErrorType.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND),
            Arguments.of(ErrorType.BAD_REQUEST, HttpStatus.BAD_REQUEST),
            Arguments.of(ErrorType.METHOD_ARGUMENT_TYPE_MISMATCH, HttpStatus.BAD_REQUEST),
            Arguments.of(ErrorType.METHOD_ARGUMENT_NOT_VALID, HttpStatus.BAD_REQUEST),
            Arguments.of(ErrorType.METHOD_ARGUMENT_CONSTRAINT_VIOLATION, HttpStatus.BAD_REQUEST),
            Arguments.of(ErrorType.MISSING_REQUEST_PARAMETER, HttpStatus.BAD_REQUEST),
            Arguments.of(ErrorType.ILLEGAL_ARGUMENT, HttpStatus.BAD_REQUEST),
            Arguments.of(ErrorType.MISSING_REQUEST_HEADER, HttpStatus.BAD_REQUEST),
            Arguments.of(ErrorType.MISSING_REQUEST_PATH_VARIABLE, HttpStatus.BAD_REQUEST)
        );
    }
}