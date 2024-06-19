package fm.mixer.gateway.error;

import fm.mixer.gateway.test.IntegrationTest;
import fm.mixer.gateway.test.container.ContainerTestBase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("SpringBootApplicationProperties")
@SpringBootTest(properties = "wiremock.server.port=8080")
class ErrorControllerAdviceIntegrationTest extends ContainerTestBase {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest(name = "{0}")
    @CsvSource({
        "onException, INTERNAL_SERVER_ERROR, 500",
        "onExternalServiceException, EXTERNAL_SERVICE_ERROR, 500",
        "onServiceUnavailableException, EXTERNAL_SERVICE_UNAVAILABLE, 503",

        "onAccessForbiddenException, ACCESS_FORBIDDEN, 403",
        "onHttpRequestMethodNotSupportedException, REQUEST_METHOD_NOT_SUPPORTED, 405",
        "onHttpMediaTypeNotAcceptableException, MEDIA_TYPE_NOT_ACCEPTABLE, 406",
        "onHttpMediaTypeNotSupportedException, MEDIA_TYPE_NOT_SUPPORTED, 415",
        "onTooManyRequestsException, TOO_MANY_REQUESTS, 429",
        "onNoHandlerFoundException, RESOURCE_NOT_FOUND, 404",
        "onResourceNotFoundException, RESOURCE_NOT_FOUND, 404",
        "onMethodArgumentTypeMismatchException, METHOD_ARGUMENT_TYPE_MISMATCH, 400",
        "onMethodArgumentTypeMismatchExceptionWithNullRequiredType, METHOD_ARGUMENT_TYPE_MISMATCH, 400",
        "onMethodArgumentNotValidException, METHOD_ARGUMENT_NOT_VALID, 400",
        "onConstraintViolationException, METHOD_ARGUMENT_CONSTRAINT_VIOLATION, 400",
        "onOpenApiRequestValidationException, METHOD_ARGUMENT_CONSTRAINT_VIOLATION, 400",
        "onMissingServletRequestParameterException, MISSING_REQUEST_PARAMETER, 400",
        "onHttpMessageNotReadableException, MISSING_REQUEST_BODY, 400",
        "onIllegalArgumentException, ILLEGAL_ARGUMENT, 400",
        "onMissingRequestHeaderException, MISSING_REQUEST_HEADER, 400",
        "onMissingPathVariableException, MISSING_REQUEST_PATH_VARIABLE, 400",
        "onOpenApiResponseValidationException, UNPROCESSABLE_ENTITY, 422",
        "onBadRequestException, BAD_REQUEST, 400"
    })
    void shouldHandleSpecificException(String url, String type, int status) throws Exception {
        // Given
        final var request = MockMvcRequestBuilders.get("/test/error-handling/" + url)
            .contentType(MediaType.APPLICATION_JSON);

        // When
        final var response = mockMvc.perform(request).andDo(print());

        // Then
        response
            .andExpect(status().is(status))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.identifier").isNotEmpty())
            .andExpect(jsonPath("$.type").value(type))
            .andExpect(jsonPath("$.description").isNotEmpty());
    }
}