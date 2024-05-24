package fm.mixer.gateway.error;

import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.error.exception.ExternalServiceException;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.error.exception.ServiceUnavailableException;
import fm.mixer.gateway.error.exception.TooManyRequestsException;
import fm.mixer.gateway.error.mapper.ErrorMapper;
import fm.mixer.gateway.model.Error;
import fm.mixer.gateway.model.ErrorType;
import fm.mixer.gateway.validation.exception.OpenApiRequestValidationException;
import fm.mixer.gateway.validation.exception.OpenApiResponseValidationException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ErrorControllerAdvice {

    private final ErrorMapper errorMapper;
    private final MessageSource messageSource;

    // *********** Server errors ***********

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> onException(final Exception ex) {
        log.error(ex.getMessage(), ex);

        return createError(ErrorType.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ExternalServiceException.class, ServiceUnavailableException.class})
    public ResponseEntity<Error> onExternalServiceException(final ExternalServiceException ex) {
        return createError(
            ex instanceof ServiceUnavailableException ? ErrorType.EXTERNAL_SERVICE_UNAVAILABLE : ErrorType.EXTERNAL_SERVICE_ERROR
        );
    }

    // *********** Client errors ***********

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Error> onHttpRequestMethodNotSupportedException() {
        return createError(ErrorType.REQUEST_METHOD_NOT_SUPPORTED);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<Error> onHttpMediaTypeNotAcceptable() {
        return createError(ErrorType.MEDIA_TYPE_NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Error> onHttpMediaTypeNotSupportedException() {
        return createError(ErrorType.MEDIA_TYPE_NOT_SUPPORTED);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<Error> onTooManyRequestsException() {
        return createError(ErrorType.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler({NoHandlerFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<Error> onResourceNotFoundException() {
        return createError(ErrorType.RESOURCE_NOT_FOUND);
    }

    // *** Client bad requests ***

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Error> onMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex) {
        final var simpleName = Optional.ofNullable(ex.getRequiredType()).map(Class::getSimpleName).orElse(null);

        return createError(ErrorType.METHOD_ARGUMENT_TYPE_MISMATCH, new Object[]{ex.getName(), simpleName});
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> onMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        return createError(ErrorType.METHOD_ARGUMENT_NOT_VALID, new Object[]{ex.getParameter().getParameterName()});
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Error> onConstraintViolationException(final ConstraintViolationException ex) {
        final var parameters = new Object[]{ex.getConstraintViolations().stream()
            .map(violation -> String.format("'%s': %s", violation.getPropertyPath().toString(), violation.getMessage()))
            .collect(Collectors.joining(", "))};

        return createError(ErrorType.METHOD_ARGUMENT_CONSTRAINT_VIOLATION, parameters);
    }

    @ExceptionHandler(OpenApiRequestValidationException.class)
    public ResponseEntity<Error> onOpenApiRequestValidationException(final OpenApiRequestValidationException ex) {
        final var parameters = new Object[]{ex.getOpenApiFieldValidations().stream()
            .map(violation -> String.format("'%s': %s", violation.path(), violation.message()))
            .collect(Collectors.joining(", "))};

        return createError(ErrorType.METHOD_ARGUMENT_CONSTRAINT_VIOLATION, parameters);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Error> onMissingServletRequestParameterException(final MissingServletRequestParameterException ex) {
        return createError(ErrorType.MISSING_REQUEST_PARAMETER, new Object[]{ex.getParameterName()});
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, IllegalArgumentException.class})
    public ResponseEntity<Error> onIllegalArgumentException() {
        return createError(ErrorType.ILLEGAL_ARGUMENT);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Error> onMissingRequestHeaderException(MissingRequestHeaderException ex) {
        return createError(ErrorType.MISSING_REQUEST_HEADER, new Object[]{ex.getHeaderName()});
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<Error> onMissingPathVariableException(final MissingPathVariableException ex) {
        return createError(ErrorType.MISSING_REQUEST_PATH_VARIABLE, new Object[]{ex.getVariableName()});
    }

    @ExceptionHandler(OpenApiResponseValidationException.class)
    public ResponseEntity<Error> onOpenApiResponseValidationException(OpenApiResponseValidationException ex) {
        final var parameters = new Object[]{ex.getOpenApiFieldValidations().stream()
            .map(violation -> String.format("'%s': %s", violation.path(), violation.message()))
            .collect(Collectors.joining(", "))};

        return createError(ErrorType.UNPROCESSABLE_ENTITY, parameters);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Error> onBadRequestException(final BadRequestException ex) {
        final var parameters = new Object[]{getErrorMessage(ex.getMessageCode(), null)};

        return createError(ErrorType.BAD_REQUEST, parameters);
    }


    private ResponseEntity<Error> createError(final ErrorType type) {
        return createError(type, null);
    }

    private ResponseEntity<Error> createError(final ErrorType type, @Nullable final Object[] parameters) {
        final var messageCode = String.join(".", ErrorType.class.getName(), type.name());
        final var message = getErrorMessage(messageCode, parameters);

        return ResponseEntity.status(errorMapper.mapToHttpStatus(type)).body(errorMapper.mapToError(type, message));
    }

    private String getErrorMessage(final String messageName, @Nullable final Object[] parameters) {
        try {
            return messageSource.getMessage(messageName, parameters, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException ignored) {
            return messageName;
        }
    }
}
