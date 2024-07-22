package fm.mixer.gateway.module.mix.api;

import fm.mixer.gateway.model.UserReaction;
import fm.mixer.gateway.module.mix.api.v1.model.SingleMix;
import fm.mixer.gateway.module.mix.api.v1.model.UserLikedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserListenedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserUploadedMixes;
import fm.mixer.gateway.test.ControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class MixControllerIntegrationTest extends ControllerIntegrationTest {

    private static final String BASE_URL = "/mixes/%s";
    private static final String SPECIFIC_MIX_URL = String.format(BASE_URL, "mid1");
    private static final String SPECIFIC_USER_URL = String.format(BASE_URL, "uid1");

    @Test
    void shouldGetSingleMix() throws Exception {
        // When
        final var response = doGetRequest(SPECIFIC_MIX_URL);

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(response, "get-mix-single.json", SingleMix.class);
    }

    @Test
    void shouldGetUserLikedMixes() throws Exception {
        // When
        final var response = doGetRequest(SPECIFIC_USER_URL + "/liked");

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(response, "get-user-liked-mix.json", UserLikedMixes.class);
    }

    @Test
    void shouldGetUserMixesHistory() throws Exception {
        // When
        final var response = doGetRequest(SPECIFIC_USER_URL + "/listened");

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(response, "get-user-listened-mix.json", UserListenedMixes.class);
    }

    @Test
    void shouldGetUserUploadedMixes() throws Exception {
        // When
        final var response = doGetRequest(SPECIFIC_USER_URL + "/uploaded");

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(response, "get-user-uploaded-mix.json", UserUploadedMixes.class);
    }

    @Test
    void shouldReactAndRemoveReactionOnMix() throws Exception {
        // Given
        final var reactionUrl = String.format(BASE_URL, "mid2/reactions");

        // React - When
        final var createResponse = doPostRequest(reactionUrl, "create-reaction.json");

        // React - Then
        assertThat(createResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(createResponse, "get-mix-reactions-like.json", UserReaction[].class);

        // Report - When
        final var createReportResponse = doPostRequest(reactionUrl, "create-report-reaction.json");

        // React - Then
        assertThat(createReportResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(createReportResponse, "get-mix-reactions-like.json", UserReaction[].class);

        // Update reaction - When
        final var updateResponse = doPostRequest(reactionUrl, "update-reaction.json");

        // Update reaction - Then
        assertThat(updateResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(updateResponse, "get-mix-reactions-dislike.json", UserReaction[].class);

        // Delete - When
        final var deleteResponse = doDeleteRequest(reactionUrl);

        // Delete - Then
        assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(deleteResponse, "get-mix-reactions-empty.json", UserReaction[].class);
    }
}