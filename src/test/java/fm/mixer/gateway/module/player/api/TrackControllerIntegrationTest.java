package fm.mixer.gateway.module.player.api;

import fm.mixer.gateway.model.UserReaction;
import fm.mixer.gateway.module.player.api.v1.model.TrackList;
import fm.mixer.gateway.test.ControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class TrackControllerIntegrationTest extends ControllerIntegrationTest {

    private static final String TRACK_REACTION_URL = "/tracks/tid2/reactions";

    @Test
    void shouldGetTrackList() throws Exception {
        // When
        final var response = doGetRequest("/tracks/mid1");

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(response, "get-track-list.json", TrackList.class);
    }

    @ParameterizedTest
    @CsvSource({
        "POST,create-like-reaction.json,get-track-reactions-like.json",
        "POST,create-recommend-reaction.json,get-track-reactions-like-recommend.json",
        "DELETE,create-like-reaction.json,get-track-reactions-recommend.json",
        "POST,update-recommend-reaction.json,get-track-reactions-do-not-recommend.json",
        "POST,update-like-reaction.json,get-track-reactions-do-not-recommend-dislike.json",
        "DELETE,update-recommend-reaction.json,get-track-reactions-dislike.json",
        "POST,create-like-reaction.json,get-track-reactions-like.json",
    })
    void shouldReactAndRemoveReactionOnTrack(String action, String request, String expected) throws Exception {
        // Given
        final var isPost = "POST".equals(action);

        // When
        final var response = isPost ? doPostRequest(TRACK_REACTION_URL, request) : doDeleteRequest(TRACK_REACTION_URL, request);

        // Then
        assertThat(response.getStatus()).isEqualTo(isPost ? HttpStatus.CREATED.value() : HttpStatus.OK.value());
        if (isPost) {
            assertThat(response.getHeader(HttpHeaders.LOCATION)).isEqualTo(TRACK_REACTION_URL);
        }
        assertResponse(response, expected, UserReaction[].class);
    }
}