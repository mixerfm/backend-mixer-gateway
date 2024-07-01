package fm.mixer.gateway.module.user.api;

import fm.mixer.gateway.module.user.api.v1.model.GetUser;
import fm.mixer.gateway.test.ControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerIntegrationTest extends ControllerIntegrationTest {

    private static final String BASE_URL = "/users";
    private static final String SPECIFIC_USER_URL = BASE_URL + "/req_uid1";

    @Test
    void shouldGetCurrentActiveUser() throws Exception {
        // When
        final var response = doGetRequest("/current-user");

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(response, "get-current-user.json", GetUser.class);
    }

    @Test
    void shouldGetUserWithoutAddress() throws Exception {
        // When
        final var response = doGetRequest(BASE_URL + "/uid2");

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void shouldDoCrudOperationsOnUser() throws Exception {
        setUserContext(new UserContext("req_uid1", "request@example.com"));

        // Create - When
        final var createResponse = doPostRequest(BASE_URL, "create-user.json");

        // Create - Then
        assertThat(createResponse.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertResponse(createResponse, "get-create-user.json", GetUser.class);
        assertGetUser("get-create-user.json");

        // Update - When
        final var updateResponse = doPutRequest(SPECIFIC_USER_URL, "update-user.json");

        // Update - Then
        assertThat(updateResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(updateResponse, "get-update-user.json", GetUser.class);
        assertGetUser("get-update-user.json");

        // Delete - When
        final var deleteResponse = doDeleteRequest(SPECIFIC_USER_URL);

        // Delete - Then
        assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(doGetRequest(SPECIFIC_USER_URL).getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    void assertGetUser(String expectedResponseFile) throws Exception {
        // Get - When
        final var getResponse = doGetRequest(SPECIFIC_USER_URL);

        // Get - Then
        assertThat(getResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(getResponse, expectedResponseFile, GetUser.class);
    }
}