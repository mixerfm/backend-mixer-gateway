package fm.mixer.gateway.error;

import fm.mixer.gateway.error.data.ErrorResponse;
import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.error.exception.ExternalServiceException;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.error.exception.ServiceUnavailableException;
import fm.mixer.gateway.error.exception.TooManyRequestsException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
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

    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> onException(Exception ex) {
        log.error(ex.getMessage(), ex);

        return createErrorResponse(ErrorCodeEnum.INTERNAL_SERVER, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> onHttpRequestMethodNotSupportedException() {
        return createErrorResponse(ErrorCodeEnum.REQUEST_METHOD_NOT_SUPPORTED, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> onMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        final var simpleName = Optional.ofNullable(ex.getRequiredType()).map(Class::getSimpleName).orElse(null);
        final var parameters = new Object[]{ex.getName(), simpleName};

        return createErrorResponse(ErrorCodeEnum.METHOD_ARGUMENT_TYPE_MISMATCH, parameters);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> onMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        final var parameters = new Object[]{ex.getParameter().getParameterName()};

        return createErrorResponse(ErrorCodeEnum.METHOD_ARGUMENT_NOT_VALID, parameters);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> onConstraintViolationException(ConstraintViolationException ex) {
        final var parameters = new Object[]{ex.getConstraintViolations().stream()
            .map(violation -> String.format("{%s: %s}", violation.getPropertyPath().toString(), violation.getMessage()))
            .collect(Collectors.joining(", "))};

        return createErrorResponse(ErrorCodeEnum.METHOD_ARGUMENT_CONSTRAINT_VIOLATION, parameters);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> onMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        final var parameters = new Object[]{ex.getParameterName()};

        return createErrorResponse(ErrorCodeEnum.MISSING_REQUEST_PARAMETER, parameters);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> onIllegalArgumentException(Exception ex) {
        log.error(ex.getMessage(), ex);

        return createErrorResponse(ErrorCodeEnum.ILLEGAL_ARGUMENT, null);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> onMissingRequestHeaderException(MissingRequestHeaderException ex) {
        final var parameters = new Object[]{ex.getHeaderName()};

        return createErrorResponse(ErrorCodeEnum.MISSING_REQUEST_HEADER, parameters);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponse> onMissingPathVariableException(MissingPathVariableException ex) {
        final var parameters = new Object[]{ex.getVariableName()};

        return createErrorResponse(ErrorCodeEnum.MISSING_REQUEST_PATH_VARIABLE, parameters);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> onNoHandlerFoundException() {
        return createErrorResponse(ErrorCodeEnum.NO_HANDLER_FOUND, null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> onResourceNotFoundException(ResourceNotFoundException ex) {
        final var parameters = new Object[]{getErrorMessage(ex.getMessageCode(), null)};

        return createErrorResponse(ErrorCodeEnum.RESOURCE_NOT_FOUND, parameters);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> onBadRequestException(BadRequestException ex) {
        final var parameters = new Object[]{getErrorMessage(ex.getMessageCode(), null)};

        return createErrorResponse(ErrorCodeEnum.BAD_REQUEST, parameters);
    }

    @ExceptionHandler({TransactionException.class, DataAccessException.class, PersistenceException.class})
    public ResponseEntity<ErrorResponse> onDatabaseException(Exception ex) {
        log.error(ex.getMessage(), ex);

        return createErrorResponse(ErrorCodeEnum.DATABASE_ERROR, null);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> onExternalServiceException(ExternalServiceException ex) {
        log.error(ex.getMessage(), ex);

        return createErrorResponse(ErrorCodeEnum.SERVICE_SERVER_ERROR, new Object[]{resolveFallbackFactoryName(ex.getFallbackFactoryName())});
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> onTooManyRequestsException(TooManyRequestsException ex) {
        log.error(ex.getMessage(), ex);

        return createErrorResponse(ErrorCodeEnum.TOO_MANY_REQUESTS, new Object[]{resolveFallbackFactoryName(ex.getFallbackFactoryName())});
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> onServiceUnavailableException(ServiceUnavailableException ex) {
        log.error(ex.getMessage(), ex);

        return createErrorResponse(ErrorCodeEnum.SERVICE_UNAVAILABLE, new Object[]{resolveFallbackFactoryName(ex.getFallbackFactoryName())});
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> onHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        log.error(ex.getMessage(), ex);

        return createErrorResponse(ErrorCodeEnum.MEDIA_TYPE_NOT_ACCEPTABLE, null);
    }

    private String resolveFallbackFactoryName(String fallbackFactoryName) {
        return getErrorMessage(String.format("fallback_factory_service.%s", fallbackFactoryName), null);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(ErrorCodeEnum errorCode, @Nullable Object[] parameters) {
        final var messageCode = String.join(".", ErrorCodeEnum.class.getName(), errorCode.name());
        final var message = getErrorMessage(messageCode, parameters);

        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.builder().status(errorCode.name()).description(message).build());
    }

    private String getErrorMessage(String messageName, @Nullable Object[] parameters) {
        try {
            return messageSource.getMessage(messageName, parameters, LocaleContextHolder.getLocale());
        }
        catch (NoSuchMessageException ignored) {
            return messageName;
        }
    }
}
