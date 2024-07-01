package fm.mixer.gateway.error;

import fm.mixer.gateway.auth.exception.AccessForbiddenException;
import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.error.exception.ExternalServiceException;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.error.exception.ServiceUnavailableException;
import fm.mixer.gateway.error.exception.TooManyRequestsException;
import fm.mixer.gateway.error.service.ErrorResponseService;
import fm.mixer.gateway.model.Error;
import fm.mixer.gateway.model.ErrorType;
import fm.mixer.gateway.validation.exception.OpenApiRequestValidationException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ErrorControllerAdvice {

    private final ErrorResponseService service;

    // *********** Server errors ***********

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> onException(final Exception ex) {
        log.error(ex.getMessage(), ex);

        return service.createError(ErrorType.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ExternalServiceException.class, ServiceUnavailableException.class})
    public ResponseEntity<Error> onExternalServiceException(final ExternalServiceException ex) {
        return service.createError(
            ex instanceof ServiceUnavailableException ? ErrorType.EXTERNAL_SERVICE_UNAVAILABLE : ErrorType.EXTERNAL_SERVICE_ERROR
        );
    }

    // *********** Client errors ***********

    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<Error> onAccessForbiddenException() {
        return service.createError(ErrorType.ACCESS_FORBIDDEN);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Error> onHttpRequestMethodNotSupportedException() {
        return service.createError(ErrorType.REQUEST_METHOD_NOT_SUPPORTED);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<Error> onHttpMediaTypeNotAcceptable() {
        return service.createError(ErrorType.MEDIA_TYPE_NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Error> onHttpMediaTypeNotSupportedException() {
        return service.createError(ErrorType.MEDIA_TYPE_NOT_SUPPORTED);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<Error> onTooManyRequestsException() {
        return service.createError(ErrorType.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler({NoHandlerFoundException.class, ResourceNotFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<Error> onResourceNotFoundException() {
        return service.createError(ErrorType.RESOURCE_NOT_FOUND);
    }

    // *** Client bad requests ***

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Error> onMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex) {
        final var simpleName = Optional.ofNullable(ex.getRequiredType()).map(Class::getSimpleName).orElse(null);

        return service.createError(ErrorType.METHOD_ARGUMENT_TYPE_MISMATCH, new Object[]{ex.getName(), simpleName});
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> onMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        final var parameters = new Object[]{ex.getFieldErrors().stream()
            .map(violation -> String.format("'%s': %s", violation.getField(), violation.getDefaultMessage()))
            .collect(Collectors.joining(", "))
        };

        return service.createError(ErrorType.METHOD_ARGUMENT_NOT_VALID, parameters);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Error> onConstraintViolationException(final ConstraintViolationException ex) {
        final var parameters = new Object[]{ex.getConstraintViolations().stream()
            .map(violation -> String.format("'%s': %s", violation.getPropertyPath().toString(), violation.getMessage()))
            .collect(Collectors.joining(", "))};

        return service.createError(ErrorType.METHOD_ARGUMENT_CONSTRAINT_VIOLATION, parameters);
    }

    @ExceptionHandler(OpenApiRequestValidationException.class)
    public ResponseEntity<Error> onOpenApiRequestValidationException(final OpenApiRequestValidationException ex) {
        final var parameters = new Object[]{ex.getOpenApiFieldValidations().stream()
            .map(violation -> String.format("'%s': %s", violation.path(), violation.message()))
            .collect(Collectors.joining(", "))};

        return service.createError(ErrorType.METHOD_ARGUMENT_CONSTRAINT_VIOLATION, parameters);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Error> onMissingServletRequestParameterException(final MissingServletRequestParameterException ex) {
        return service.createError(ErrorType.MISSING_REQUEST_PARAMETER, new Object[]{ex.getParameterName()});
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Error> onHttpMessageNotReadableException() {
        return service.createError(ErrorType.MISSING_REQUEST_BODY);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> onIllegalArgumentException(Exception e) {
        log.error(e.getMessage(), e);

        return service.createError(ErrorType.ILLEGAL_ARGUMENT);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Error> onMissingRequestHeaderException(MissingRequestHeaderException ex) {
        return service.createError(ErrorType.MISSING_REQUEST_HEADER, new Object[]{ex.getHeaderName()});
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<Error> onMissingPathVariableException(final MissingPathVariableException ex) {
        return service.createError(ErrorType.MISSING_REQUEST_PATH_VARIABLE, new Object[]{ex.getVariableName()});
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Error> onBadRequestException(final BadRequestException ex) {
        final var parameters = new Object[]{service.getErrorMessage(ex.getMessageCode(), null)};

        return service.createError(ErrorType.BAD_REQUEST, parameters);
    }
}
