package fm.mixer.gateway;

import fm.mixer.gateway.test.UnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@UnitTest
class MixerGatewayApplicationUnitTest {

    @Test
    void shouldRunMixerGatewayApplication() {
        // Given
        final var params = new String[]{"--spring.main.web-environment=false"};
        try (final var application = mockStatic(SpringApplication.class)) {
            application.when(() -> SpringApplication.run(MixerGatewayApplication.class, params))
                .thenReturn(mock(ConfigurableApplicationContext.class));

            // When & Then
            assertThatCode(() -> MixerGatewayApplication.main(params)).doesNotThrowAnyException();
        }
    }
}
