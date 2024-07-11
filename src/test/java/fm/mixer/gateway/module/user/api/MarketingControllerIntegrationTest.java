package fm.mixer.gateway.module.user.api;

import fm.mixer.gateway.module.user.persistance.repository.UserNewsletterRepository;
import fm.mixer.gateway.test.ControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class MarketingControllerIntegrationTest extends ControllerIntegrationTest {

    private static final String NEWSLETTER_URL = "/newsletters/subscription";

    @Autowired
    private UserNewsletterRepository repository;

    @Test
    void shouldDoCrudOperationsOnNewsletter() throws Exception {
        // Empty state
        assertThat(repository.findAll()).isEmpty();

        // Subscribe - When
        final var subscribeResponse = doPostRequest(NEWSLETTER_URL, "subscribe.json");

        // Subscribe - Then
        assertThat(subscribeResponse.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(subscribeResponse.getHeader(HttpHeaders.LOCATION)).startsWith("/newsletters/subscription/");

        setIdentifier();

        // Unsubscribe - When
        final var unsubscribeResponse = doDeleteRequest(NEWSLETTER_URL, "unsubscribe.json");

        // Unsubscribe - Then
        assertThat(unsubscribeResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(repository.findAll()).isEmpty();
    }

    private void setIdentifier() {
        final var subscriptionDetails = repository.findAll().getFirst();
        subscriptionDetails.setIdentifier("3c6dcd15-8aa2-4e1e-8d11-44e611edc9af");
        repository.save(subscriptionDetails);
    }
}