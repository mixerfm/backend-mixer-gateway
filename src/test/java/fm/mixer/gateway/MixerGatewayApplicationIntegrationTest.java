package fm.mixer.gateway;

import fm.mixer.gateway.test.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MixerGatewayApplicationIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldLoadContext() {
        final var contextLoaded = true;

        assertThat(contextLoaded).isTrue();
    }
}