package fm.mixer.gateway.module.player.api;

import fm.mixer.gateway.module.player.api.v1.model.Session;
import fm.mixer.gateway.test.ControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class SessionControllerIntegrationTest extends ControllerIntegrationTest {

    @Test
    void shouldGetPlayerSession() throws Exception {
        // When
        final var response = doGetRequest("/player-session");

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(response, "get-current-session.json", Session.class);
    }
}