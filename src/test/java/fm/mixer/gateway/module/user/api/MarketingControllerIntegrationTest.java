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
    private static final String EMAIL = "newsletter-test@example.com";
    private static final String IDENTIFIER = "3c6dcd15-8aa2-4e1e-8d11-44e611edc9af";

    @Autowired
    private UserNewsletterRepository repository;

    @Test
    void shouldDoCrudOperationsOnNewsletter() throws Exception {
        // Subscribe - When
        final var subscribeResponse = doPostRequest(NEWSLETTER_URL, "subscribe.json");

        // Subscribe - Then
        assertThat(subscribeResponse.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(subscribeResponse.getHeader(HttpHeaders.LOCATION)).startsWith(NEWSLETTER_URL);

        setIdentifier();

        // Unsubscribe - When
        final var unsubscribeResponse = doDeleteRequest(NEWSLETTER_URL, "unsubscribe.json");

        // Unsubscribe - Then
        assertThat(unsubscribeResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(repository.findAll()).allSatisfy(newsletter -> {
            assertThat(newsletter.getIdentifier()).isNotEqualTo(IDENTIFIER);
            assertThat(newsletter.getEmail()).isNotEqualTo(EMAIL);
        });
    }

    private void setIdentifier() {
        final var subscriptionDetails = repository.findAll().stream()
            .filter(detail -> detail.getEmail().equals(EMAIL))
            .findFirst()
            .orElseThrow();

        subscriptionDetails.setIdentifier(IDENTIFIER);

        repository.save(subscriptionDetails);
    }
}