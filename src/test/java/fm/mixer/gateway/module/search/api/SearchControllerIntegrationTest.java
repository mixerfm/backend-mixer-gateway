package fm.mixer.gateway.module.search.api;

import fm.mixer.gateway.module.search.api.v1.model.SearchItemResultGroupList;
import fm.mixer.gateway.test.ControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class SearchControllerIntegrationTest extends ControllerIntegrationTest {

    @Test
    void shouldGetSearchAllResults() throws Exception {
        // When
        final var response = doGetRequest("/search/all?query=arc");

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(response, "get-search-all.json", SearchItemResultGroupList.class);
    }
}