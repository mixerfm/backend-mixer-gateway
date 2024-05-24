package fm.mixer.gateway.error.stub;

import fm.mixer.gateway.error.exception.BadRequestException;
import fm.mixer.gateway.error.exception.ExternalServiceException;
import fm.mixer.gateway.error.exception.ResourceNotFoundException;
import fm.mixer.gateway.error.exception.ServiceUnavailableException;
import fm.mixer.gateway.error.exception.TooManyRequestsException;
import fm.mixer.gateway.validation.exception.OpenApiRequestValidationException;
import fm.mixer.gateway.validation.exception.OpenApiResponseValidationException;
import fm.mixer.gateway.validation.exception.model.OpenApiFieldValidation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test/error-handling")
public class ErrorControllerAdviceTestController {

    private static final String IRRELEVANT_DATA = "some data";
    public static final String TEST_PATH = "testPath";
    public static final String TEST_TYPE = "LocalDate";

    @Autowired
    private Validator validator;

    @GetMapping("onException")
    public void onException() {
        throw new RuntimeException(IRRELEVANT_DATA);
    }

    @GetMapping("onExternalServiceException")
    public void onExternalServiceException() {
        throw new ExternalServiceException();
    }

    @GetMapping("onServiceUnavailableException")
    public void onServiceUnavailableException() {
        throw new ServiceUnavailableException();
    }

    // Client errors

    @GetMapping("onHttpRequestMethodNotSupportedException")
    public void onHttpRequestMethodNotSupportedException() throws HttpRequestMethodNotSupportedException {
        throw new HttpRequestMethodNotSupportedException(IRRELEVANT_DATA);
    }

    @GetMapping("onHttpMediaTypeNotAcceptableException")
    public void onHttpMediaTypeNotAcceptableException() throws HttpMediaTypeNotAcceptableException {
        throw new HttpMediaTypeNotAcceptableException(IRRELEVANT_DATA);
    }

    @GetMapping("onHttpMediaTypeNotSupportedException")
    public void onHttpMediaTypeNotSupportedException() throws HttpMediaTypeNotSupportedException {
        throw new HttpMediaTypeNotSupportedException(IRRELEVANT_DATA);
    }

    @GetMapping("onTooManyRequestsException")
    public void onTooManyRequestsException() {
        throw new TooManyRequestsException();
    }

    @GetMapping("onNoHandlerFoundException")
    public void onNoHandlerFoundException() throws NoHandlerFoundException {
        throw new NoHandlerFoundException(IRRELEVANT_DATA, IRRELEVANT_DATA, new HttpHeaders());
    }

    @GetMapping("onResourceNotFoundException")
    public void onResourceNotFoundException() {
        throw new ResourceNotFoundException();
    }

    // Client bad requests

    @GetMapping("onMethodArgumentTypeMismatchException")
    public void onMethodArgumentTypeMismatchException() throws NoSuchMethodException {
        throw new MethodArgumentTypeMismatchException(null, LocalDate.class, "", getMethodParameter(), new Throwable());
    }

    @GetMapping("onMethodArgumentTypeMismatchExceptionWithNullRequiredType")
    public void onMethodArgumentTypeMismatchExceptionWithNullRequiredType() throws NoSuchMethodException {
        throw new MethodArgumentTypeMismatchException(null, null, TEST_PATH, getMethodParameter(), new Throwable());
    }

    @GetMapping("onMethodArgumentNotValidException")
    public void onMethodArgumentNotValidException() throws MethodArgumentNotValidException, NoSuchMethodException {
        throw new MethodArgumentNotValidException(getMethodParameter(), new MapBindingResult(Map.of(), ""));
    }

    @GetMapping("onConstraintViolationException")
    public void onConstraintViolationException() {
        throw new ConstraintViolationException(IRRELEVANT_DATA, validator.validateValue(InvalidArgumentsTestStub.class, TEST_PATH, -1));
    }

    @GetMapping("onOpenApiRequestValidationException")
    public void onOpenApiRequestValidationException() {
        throw new OpenApiRequestValidationException(List.of(new OpenApiFieldValidation(TEST_PATH, IRRELEVANT_DATA)));
    }

    @GetMapping("onMissingServletRequestParameterException")
    public void onMissingServletRequestParameterException() throws MissingServletRequestParameterException {
        throw new MissingServletRequestParameterException(TEST_PATH, TEST_TYPE);
    }

    @GetMapping("onHttpMessageNotReadableException")
    public void onHttpMessageNotReadableException() {
        throw new HttpMessageNotReadableException(IRRELEVANT_DATA, new MockHttpInputMessage(IRRELEVANT_DATA.getBytes(StandardCharsets.UTF_8)));
    }

    @GetMapping("onIllegalArgumentException")
    public void onIllegalArgumentException() {
        throw new IllegalArgumentException();
    }

    @GetMapping("onMissingRequestHeaderException")
    public void onMissingRequestHeaderException() throws MissingRequestHeaderException, NoSuchMethodException {
        throw new MissingRequestHeaderException(TEST_PATH, getMethodParameter());
    }

    @GetMapping("onMissingPathVariableException")
    public void onMissingPathVariableException() throws MissingPathVariableException, NoSuchMethodException {
        throw new MissingPathVariableException(TEST_PATH, getMethodParameter());
    }

    @GetMapping("onOpenApiResponseValidationException")
    public void onOpenApiResponseValidationException() {
        throw new OpenApiResponseValidationException(List.of(new OpenApiFieldValidation(TEST_PATH, IRRELEVANT_DATA)));
    }

    @GetMapping("onBadRequestException")
    public void onBadRequestException() {
        throw new BadRequestException(IRRELEVANT_DATA);
    }

    private MethodParameter getMethodParameter() throws NoSuchMethodException {
        final var method = this.getClass().getMethod("something", LocalDate.class);
        final var methodParameter = new MethodParameter(method, 0);

        // Bind found parameters to internal structures
        methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());

        return methodParameter;
    }

    @SuppressWarnings({"unused", "EmptyMethod"})
    public void something(LocalDate testPath) {
    }
}
