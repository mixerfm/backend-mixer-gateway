package fm.mixer.gateway.module.app.api;

import fm.mixer.gateway.module.app.api.v1.model.VersionList;
import fm.mixer.gateway.test.ControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationControllerIntegrationTest extends ControllerIntegrationTest {

    @Test
    void shouldGetVersionList() throws Exception {
        // When
        final var response = doGetRequest("/versions");

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(response, "get-versions.json", VersionList.class);
    }
}