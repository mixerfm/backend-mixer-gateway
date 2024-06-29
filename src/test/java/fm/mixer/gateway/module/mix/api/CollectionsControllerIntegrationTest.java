package fm.mixer.gateway.module.mix.api;

import fm.mixer.gateway.module.mix.api.v1.model.CollectionList;
import fm.mixer.gateway.module.mix.api.v1.model.SingleCollection;
import fm.mixer.gateway.test.ControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionsControllerIntegrationTest extends ControllerIntegrationTest {

    private static final String BASE_URL = "/collections";
    private static final String SPECIFIC_COLLECTION_URL = BASE_URL + "/cid1";

    @Test
    void shouldGetCollectionList() throws Exception {
        // When
        final var response = doGetRequest(BASE_URL);

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(response, "get-collection-list.json", CollectionList.class);
    }

    @Test
    void shouldGetCollectionListWithMixes() throws Exception {
        // When
        final var response = doGetRequest(BASE_URL, Map.of("mixCount", "1"));

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(response, "get-collection-list-with-mixes.json", CollectionList.class);
    }

    @Test
    void shouldGetSingleCollection() throws Exception {
        // When
        final var response = doGetRequest(SPECIFIC_COLLECTION_URL);

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(response, "get-collection-single.json", SingleCollection.class);
    }

    /*
    @Test
    void shouldReactAndRemoveReactionOnMix() throws Exception {
        // Given
        final var reactionUrl = String.format(BASE_URL, "cid2/reactions");

        // React - When
        final var createResponse = doPostRequest(reactionUrl, "create-reaction.json");

        // React - Then
        assertThat(createResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertReaction(List.of(UserReaction.TypeEnum.LIKE));

        // Report - When
        final var createReportResponse = doPostRequest(reactionUrl, "create-report-reaction.json");

        // React - Then
        assertThat(createReportResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertReaction(List.of(UserReaction.TypeEnum.LIKE));

        // Update reaction - When
        final var updateResponse = doPostRequest(reactionUrl, "update-reaction.json");

        // Update reaction - Then
        assertThat(updateResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertReaction(List.of(UserReaction.TypeEnum.DISLIKE));

        // Delete - When
        final var deleteResponse = doDeleteRequest(reactionUrl);

        // Delete - Then
        assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertReaction(List.of());
    }

    private void assertReaction(List<UserReaction.TypeEnum> reactions) throws Exception {
        final var collection = mapper.readValue(doGetRequest(String.format(BASE_URL, "cid2")).getContentAsString(), SingleCollection.class);

        assertThat(collection.getReactions()).containsExactlyInAnyOrderElementsOf(reactions.stream().map(UserReaction::new).toList());
    }*/
}