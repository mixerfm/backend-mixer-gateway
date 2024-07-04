package fm.mixer.gateway.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import static java.lang.System.lineSeparator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@AutoConfigureMockMvc
public abstract class ControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    protected ObjectMapper mapper;
    @Autowired
    private WireMockServer wireMockServer;
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private MockMvc mvc;

    public record UserContext(String userId, String email, String device) {
    }

    private final UserContext DEFAULT_CONTEXT = new UserContext("uid1", "request@example.com", "dev1");
    private UserContext currentContext = DEFAULT_CONTEXT;

    @AfterEach
    void cleanup() {
        currentContext = DEFAULT_CONTEXT;
        wireMockServer.resetAll();
    }

    public void setUserContext(UserContext userContext) {
        currentContext = userContext;
    }

    /**
     * Make GET request
     *
     * @param url - defined in specification (or in Api interface from which we implement controller delegate)
     * @return MockHttpServletResponse which contains body and http status
     */
    protected MockHttpServletResponse doGetRequest(String url) throws Exception {
        return doRequest(HttpMethod.GET, url, Map.of(), null);
    }

    /**
     * Make GET request with query parameters
     *
     * @param url             - defined in RICE specification (or in Api interface from which we implement controller delegate)
     * @param queryParameters - query parameters which are sent to endpoint
     * @return MockHttpServletResponse which contains body and http status
     */
    protected MockHttpServletResponse doGetRequest(String url, Map<String, String> queryParameters) throws Exception {
        return doRequest(HttpMethod.GET, url, queryParameters, null);
    }

    /**
     * Make POST request
     *
     * @param url - defined in specification (or in Api interface from which we implement controller delegate)
     * @return MockHttpServletResponse which contains body and http status
     */
    protected MockHttpServletResponse doPostRequest(String url) throws Exception {
        return doRequest(HttpMethod.POST, url, Map.of(), null);
    }

    /**
     * Make POST request with body
     *
     * @param url             - defined in specification (or in Api interface from which we implement controller delegate)
     * @param requestBodyFile - name of the file in test resource directory where request body is stored
     * @return MockHttpServletResponse which contains body and http status
     */
    protected MockHttpServletResponse doPostRequest(String url, String requestBodyFile) throws Exception {
        return doRequest(HttpMethod.POST, url, Map.of(), readAsString("request/" + requestBodyFile).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Make PUT request
     *
     * @param url             - defined in specification (or in Api interface from which we implement controller delegate)
     * @param requestBodyFile - name of the file in test resource directory where request body is stored
     * @return MockHttpServletResponse which contains body and http status
     */
    protected MockHttpServletResponse doPutRequest(String url, String requestBodyFile) throws Exception {
        return doRequest(HttpMethod.PUT, url, Map.of(), readAsString("request/" + requestBodyFile).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Make DELETE request
     *
     * @param url - defined in specification (or in Api interface from which we implement controller delegate)
     * @return MockHttpServletResponse which contains body and http status
     */
    protected MockHttpServletResponse doDeleteRequest(String url) throws Exception {
        return doRequest(HttpMethod.DELETE, url, Map.of(), null);
    }

    /**
     * Make DELETE request with body
     *
     * @param url             - defined in specification (or in Api interface from which we implement controller delegate)
     * @param requestBodyFile - name of the file in test resource directory where request body is stored
     * @return MockHttpServletResponse which contains body and http status
     */
    protected MockHttpServletResponse doDeleteRequest(String url, String requestBodyFile) throws Exception {
        return doRequest(HttpMethod.DELETE, url, Map.of(), readAsString("request/" + requestBodyFile).getBytes(StandardCharsets.UTF_8));
    }

    private MockHttpServletResponse doRequest(HttpMethod method, String url, Map<String, String> queryParameters, byte[] requestBody) throws Exception {
        final var request = MockMvcRequestBuilders.request(method, url)
            .contentType(APPLICATION_JSON)
            .header("X-Idempotency-Key", UUID.randomUUID())
            .header("X-User-Id", currentContext.userId)
            .header("X-User-Email", currentContext.email)
            .header("X-Device-Id", currentContext.device)
            .characterEncoding(UTF_8)
            .content(requestBody);

        queryParameters.forEach(request::queryParam);

        return mvc.perform(request)
            .andDo(print())
            .andReturn()
            .getResponse();
    }

    /**
     * Assert given MvcResult against expected response in test directory file
     *
     * @param result               - result of previous HTTP request on Gateway
     * @param expectedResponseFile - file in test directory where expected response is stored
     */
    protected void assertResponse(MockHttpServletResponse result, String expectedResponseFile, Class<?> responseType) throws IOException {
        final var response = mapper.readValue(result.getContentAsString(UTF_8), responseType);
        final var expectedResponse = mapper.readValue(readAsString(expectedResponseFile), responseType);

        assertThat(response).isEqualTo(expectedResponse);
    }

    private String readAsString(String filename) throws IOException {
        final var matcher = Pattern.compile(".*module\\.(?<dir>.*)\\.api.*").matcher(this.getClass().getPackageName());

        if (!matcher.find()) {
            throw new IOException("Test directory not found.");
        }

        final var resource = resourceLoader.getResource(String.format("classpath:api/%s/%s", matcher.group("dir"), filename));

        try (final var br = new BufferedReader(new InputStreamReader(resource.getInputStream(), UTF_8))) {
            return br.lines().collect(joining(lineSeparator()));
        }
    }
}
