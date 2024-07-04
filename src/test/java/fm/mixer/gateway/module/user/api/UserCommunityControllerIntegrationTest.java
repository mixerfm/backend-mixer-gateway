package fm.mixer.gateway.module.user.api;

import fm.mixer.gateway.module.user.api.v1.model.GetUserList;
import fm.mixer.gateway.test.ControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class UserCommunityControllerIntegrationTest extends ControllerIntegrationTest {

    private static final String USER_1_URL = "/users/uid1";
    private static final String USER_2_URL = "/users/uid2";

    @Test
    void shouldDoCrudOperationsOnFollower() throws Exception {
        // Empty state
        assertFollowList("/followers", "get-empty-list.json");
        assertFollowList("/following", "get-empty-list.json");

        // Follow - When
        final var followResponse = doPostRequest(USER_2_URL + "/follow");

        // Follow - Then
        assertThat(followResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertFollowList("/followers", "get-follow-list.json");
        assertFollowList("/following", "get-empty-list.json");

        // Switch to user 2
        setUserContext(new UserContext("uid2", "info2@example.com", "dev1"));

        // FollowBack - When
        final var followBackResponse = doPostRequest(USER_1_URL + "/follow");

        // Switch to user 1
        setUserContext(new UserContext("uid1", "info@example.com", "dev1"));

        // FollowBack - Then
        assertThat(followBackResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertFollowList("/followers", "get-follow-list.json");
        assertFollowList("/following", "get-follow-list.json");

        // Unfollow - When
        final var unfollowResponse = doPostRequest(USER_2_URL + "/unfollow");

        // Unfollow - Then
        assertThat(unfollowResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertFollowList("/followers", "get-empty-list.json");
        assertFollowList("/following", "get-follow-list.json");

        // Remove Follower - When
        final var removeFollowerResponse = doPostRequest(USER_2_URL + "/remove-follower");

        // Remove Follower - Then
        assertThat(removeFollowerResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertFollowList("/followers", "get-empty-list.json");
        assertFollowList("/following", "get-empty-list.json");
    }

    @Test
    void react() throws Exception {
        // When
        final var response = doPostRequest(USER_2_URL + "/reactions", "create-report-reaction.json");

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void assertFollowList(String endpoint, String expectedResponseFile) throws Exception {
        // When
        final var response = doGetRequest(USER_2_URL + endpoint);

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(response, expectedResponseFile, GetUserList.class);
    }
}