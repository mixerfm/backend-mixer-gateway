package fm.mixer.gateway.module.player.api;

import fm.mixer.gateway.module.player.api.v1.model.TrackList;
import fm.mixer.gateway.module.player.persistance.repository.MixTrackLikeRepository;
import fm.mixer.gateway.module.player.persistance.repository.MixTrackRepository;
import fm.mixer.gateway.module.user.persistance.repository.UserRepository;
import fm.mixer.gateway.test.ControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class TrackControllerIntegrationTest extends ControllerIntegrationTest {

    private static final String TRACK_REACTION_URL = "/tracks/tid2/reactions";

    @Autowired
    private MixTrackLikeRepository likeRepository;

    @Autowired
    private MixTrackRepository trackRepository;

    @Autowired
    private UserRepository userRepository;

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
        "POST,create-like-reaction.json,true,",
        "POST,create-recommend-reaction.json,true,true",
        "DELETE,create-like-reaction.json,,true",
        "POST,update-recommend-reaction.json,,false",
        "POST,update-like-reaction.json,false,false",
        "DELETE,update-recommend-reaction.json,false,",
    })
    void shouldReactAndRemoveReactionOnTrack(String action, String request, Boolean like, Boolean recommend) throws Exception {
        // When
        final var likeResponse = "POST".equals(action) ? doPostRequest(TRACK_REACTION_URL, request) : doDeleteRequest(TRACK_REACTION_URL, request);

        // Then
        assertThat(likeResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertReaction(like, recommend);
    }

    private void assertReaction(Boolean like, Boolean recommend) {
        final var trackLike = likeRepository.findByUserAndTrack(userRepository.getReferenceById(1L), trackRepository.getReferenceById(2L))
            .orElseThrow();

        assertThat(trackLike.getLiked()).isEqualTo(like);
        assertThat(trackLike.getRecommend()).isEqualTo(recommend);
    }
}