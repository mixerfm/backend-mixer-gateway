package fm.mixer.gateway.module.player.api;

import fm.mixer.gateway.module.player.api.v1.model.Track;
import fm.mixer.gateway.test.ControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerControllerIntegrationTest extends ControllerIntegrationTest {

    private static final String PLAYER_ON_MIX_URL = "/player/mid3";

    @Test
    void shouldChangeTracksBasedOnActions() throws Exception {
        // Play - When
        final var playResponse = doPostRequest(PLAYER_ON_MIX_URL + "/play");

        // Play - Then
        assertThat(playResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(playResponse, "get-player-first-track.json", Track.class);

        // Next - When
        final var nextResponse = doPostRequest(PLAYER_ON_MIX_URL + "/next");

        // Next - Then
        assertThat(nextResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(nextResponse, "get-player-second-track.json", Track.class);

        // Skip - When
        final var skipResponse = doPostRequest(PLAYER_ON_MIX_URL + "/skip");

        // Skip - Then
        assertThat(skipResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(skipResponse, "get-player-third-track.json", Track.class);

        // Invalid Next - When
        final var invalidNextResponse = doPostRequest(PLAYER_ON_MIX_URL + "/next");

        // Invalid Next - Then
        assertThat(invalidNextResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());

        // Invalid Skip - When
        final var invalidSkipResponse = doPostRequest(PLAYER_ON_MIX_URL + "/skip");

        // Invalid Skip - Then
        assertThat(invalidSkipResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());

        // Play Again - When
        final var playAgainResponse = doPostRequest(PLAYER_ON_MIX_URL + "/play");

        // Play Again - Then
        assertThat(playAgainResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(playAgainResponse, "get-player-first-track.json", Track.class);

        // Cleanup
        doPostRequest("/player/mid1");
    }

    @Test
    void pauseTrack() throws Exception {
        // When
        final var response = doPostRequest(PLAYER_ON_MIX_URL + "/pause");

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldChangeTrackVolume() throws Exception {
        // When
        final var response = doPostRequest(PLAYER_ON_MIX_URL + "/change-volume", "change-volume.json");

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}