package fm.mixer.gateway.module.community.api;

import fm.mixer.gateway.module.community.api.v1.model.Comment;
import fm.mixer.gateway.module.community.api.v1.model.CommentList;
import fm.mixer.gateway.module.community.api.v1.model.UserReaction;
import fm.mixer.gateway.test.ControllerIntegrationTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentControllerIntegrationTest extends ControllerIntegrationTest {

    private static final String COMMENT_BASE_URL = "/comments/%s";
    private static final String COMMENT_ON_MIX_URL = String.format(COMMENT_BASE_URL, "mid1");
    private static final String COMMENT_ON_COMMENT_URL = String.format(COMMENT_BASE_URL, "cmid1");

    @Test
    @Order(1)
    void shouldGetCommentListResponse() throws Exception {
        // When
        final var response = doGetRequest(COMMENT_ON_MIX_URL);

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertResponse(response, "get-comments.json", CommentList.class);
    }

    @Test
    void shouldDoCrudOperationsOnComment() throws Exception {
        // Create - When
        final var createResponse = doPostRequest(COMMENT_ON_MIX_URL, "create-comment.json");

        // Create - Then
        assertThat(createResponse.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        final var createComment = mapper.readValue(createResponse.getContentAsString(), Comment.class);
        assertComment(createComment, "Test Comment Created");

        // Fetch after create - When
        final var fetchAfterCreateResponse = doGetRequest(COMMENT_ON_MIX_URL);

        // Fetch after create - Then
        final var fetchAfterCreateCommentList = mapper.readValue(fetchAfterCreateResponse.getContentAsString(), CommentList.class);
        assertThat(fetchAfterCreateCommentList.getComments()).anyMatch(comment -> comment.getIdentifier().equals(createComment.getIdentifier()));

        // Update - When
        final var updateResponse = doPutRequest(String.format(COMMENT_BASE_URL, createComment.getIdentifier()), "update-comment.json");

        // Update - Then
        assertThat(updateResponse.getStatus()).isEqualTo(HttpStatus.OK.value());

        final var updateComment = mapper.readValue(updateResponse.getContentAsString(), Comment.class);
        assertThat(updateComment.getIdentifier()).isEqualTo(createComment.getIdentifier());
        assertComment(updateComment, "Test Comment Updated");

        // Fetch after update - When
        final var fetchAfterUpdateResponse = doGetRequest(COMMENT_ON_MIX_URL);

        // Fetch after create - Then
        final var fetchAfterUpdateCommentList = mapper.readValue(fetchAfterUpdateResponse.getContentAsString(), CommentList.class);
        assertThat(fetchAfterUpdateCommentList.getComments()).anyMatch(comment -> comment.getIdentifier().equals(updateComment.getIdentifier()));

        // Delete - When
        final var deleteResponse = doDeleteRequest(String.format(COMMENT_BASE_URL, createComment.getIdentifier()));

        // Delete - Then
        assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // Fetch after delete - When
        final var fetchAfterDeleteResponse = doGetRequest(COMMENT_ON_MIX_URL);

        // Fetch after delete - Then
        final var fetchAfterDeleteCommentList = mapper.readValue(fetchAfterDeleteResponse.getContentAsString(), CommentList.class);
        assertThat(fetchAfterDeleteCommentList.getComments()).noneMatch(comment -> comment.getIdentifier().equals(updateComment.getIdentifier()));
    }

    @Test
    void shouldCreateAndFetchReplyOnComment() throws Exception {
        // Create - When
        final var createResponse = doPostRequest(COMMENT_ON_COMMENT_URL + "/replies", "create-comment.json");
        
        // Create - Then
        assertThat(createResponse.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        final var createComment = mapper.readValue(createResponse.getContentAsString(), Comment.class);
        assertComment(createComment, "Test Comment Created");
        
        // Fetch - When
        final var repliesResponse = doGetRequest(COMMENT_ON_COMMENT_URL + "/replies");
        
        // Fetch - Then
        final var repliesCommentList = mapper.readValue(repliesResponse.getContentAsString(), CommentList.class);
        assertThat(repliesCommentList.getComments()).anyMatch(comment -> comment.getIdentifier().equals(createComment.getIdentifier()));
    }

    @Test
    void shouldReactAndRemoveReactionOnComment() throws Exception {
        // Given
        final var comment = mapper.readValue(doPostRequest(COMMENT_ON_MIX_URL, "create-comment.json").getContentAsString(), Comment.class);

        assertThat(comment.getReactions()).isEmpty(); // pre-check

        // React - When
        final var createResponse = doPostRequest(String.format(COMMENT_BASE_URL + "/reactions", comment.getIdentifier()), "create-reaction.json");

        // React - Then
        assertThat(createResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertReaction(comment.getIdentifier(), List.of(UserReaction.TypeEnum.LIKE));

        // Report - When
        final var createReportResponse = doPostRequest(String.format(COMMENT_BASE_URL + "/reactions", comment.getIdentifier()), "create-report-reaction.json");

        // React - Then
        assertThat(createReportResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertReaction(comment.getIdentifier(), List.of(UserReaction.TypeEnum.LIKE));

        // Update reaction - When
        final var updateResponse = doPostRequest(String.format(COMMENT_BASE_URL + "/reactions", comment.getIdentifier()), "update-reaction.json");

        // Update reaction - Then
        assertThat(updateResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertReaction(comment.getIdentifier(), List.of(UserReaction.TypeEnum.DISLIKE));

        // Delete - When
        final var deleteResponse = doDeleteRequest(String.format(COMMENT_BASE_URL + "/reactions", comment.getIdentifier()));

        // Delete - Then
        assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertReaction(comment.getIdentifier(), List.of());
    }

    @Test
    void shouldThrowBadRequestOnUnsupportedReaction() throws Exception {
        // When
        final var response = doPostRequest(COMMENT_ON_COMMENT_URL + "/reactions", "bad-reaction.json");

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Provided user reaction type is not supported. Please consult with endpoint documentation and try again.");
    }

    private void assertComment(Comment comment, String content) {
        assertThat(comment.getIdentifier()).isNotBlank();
        assertThat(comment.getContent()).isEqualTo(content);
        assertThat(comment.getAuthor().getUsername()).isEqualTo("uid1");
        assertThat(comment.getNumberOfReplies()).isZero();
        assertThat(comment.getNumberOfLikes()).isZero();
        assertThat(comment.getNumberOfDislikes()).isZero();
        assertThat(comment.getReactions()).isEmpty();
        assertThat(comment.getCreatedDateTime()).isNotNull();
        assertThat(comment.getUpdatedDateTime()).isNotNull();
    }

    private void assertReaction(String identifier, List<UserReaction.TypeEnum> reactions) throws Exception {
        final var comment = mapper.readValue(doGetRequest(COMMENT_ON_MIX_URL).getContentAsString(), CommentList.class).getComments()
            .stream().filter(c -> c.getIdentifier().equals(identifier)).findFirst().orElseThrow();

        assertThat(comment.getReactions()).containsExactlyInAnyOrderElementsOf(reactions.stream().map(UserReaction::new).toList());
    }
}