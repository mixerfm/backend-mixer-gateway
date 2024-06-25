package fm.mixer.gateway.module.community.mapper;

import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.module.community.api.v1.model.UserReaction;
import fm.mixer.gateway.module.community.persistance.entity.Comment;
import fm.mixer.gateway.module.community.persistance.entity.CommentLike;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.test.UnitTest;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@UnitTest
class CommunityMapperUnitTest {

    private final CommunityMapper mapper = Mappers.getMapper(CommunityMapper.class);

    @Test
    void shouldMapToComment() {
        // given
        final var comment = Instancio.create(Comment.class);
        final var commentLike = comment.getLikes().stream().findFirst().orElseThrow();
        commentLike.setLiked(true);

        try (final var user = mockStatic(UserPrincipalUtil.class)) {
            user.when(UserPrincipalUtil::getCurrentActiveUser).thenReturn(Optional.of(commentLike.getUser()));

            // When
            final var result = mapper.toComment(comment);

            // Then
            assertThat(result.getContent()).isEqualTo(comment.getContent());
            assertThat(result.getIdentifier()).isEqualTo(comment.getIdentifier());
            assertThat(result.getCreatedDateTime()).isEqualTo(comment.getCreatedAt());
            assertThat(result.getUpdatedDateTime()).isEqualTo(comment.getUpdatedAt());
            assertThat(result.getNumberOfReplies()).isEqualTo(comment.getNumberOfReplies());
            assertThat(result.getNumberOfLikes()).isEqualTo(comment.getLikes().stream().filter(CommentLike::getLiked).count());
            assertThat(result.getNumberOfDislikes()).isEqualTo(comment.getLikes().stream().filter(like -> !like.getLiked()).count());

            assertThat(result.getReactions())
                .hasSize(1)
                .allMatch(reaction -> UserReaction.TypeEnum.LIKE.equals(reaction.getType()));

            // Author
            assertThat(result.getAuthor().getUsername()).isEqualTo(comment.getUser().getIdentifier());
            assertThat(result.getAuthor().getDisplayName()).isEqualTo(comment.getUser().getName());
            assertThat(result.getAuthor().getAvatarUrl()).isEqualTo(comment.getUser().getAvatar());
            assertThat(result.getAuthor().getProfileColor()).isEqualTo(comment.getUser().getProfileColor());
            assertThat(result.getAuthor().getActive()).isEqualTo(comment.getUser().getActive());
        }
    }

    @Test
    void shouldMapToCommentList() {
        // Given
        final var items = Instancio.createList(Comment.class);
        final var paginationRequest = PaginationMapper.toPaginationRequest(1, 2, List.of());

        try (final var user = mockStatic(UserPrincipalUtil.class)) {
            user.when(UserPrincipalUtil::getCurrentActiveUser).thenReturn(Optional.empty());

            // When
            final var result = mapper.toCommentList(new PageImpl<>(items), paginationRequest);

            // Then
            assertThat(result.getComments()).hasSize(items.size()).allMatch(comment -> comment.getReactions().isEmpty());
            assertThat(result.getMetadata()).isNotNull();
        }
    }

    @Test
    void shouldMapToCommentLikeEntity() {
        // Given
        final var user = Instancio.create(User.class);
        final var comment = Instancio.create(Comment.class);

        // When
        final var result = mapper.toCommentLikeEntity(user, comment, true);

        // Then
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getComment()).isEqualTo(comment);
        assertThat(result.getLiked()).isTrue();

        assertThat(result.getId()).isNull();
        assertThat(result.getUpdatedAt()).isNull();
    }

    @Test
    void shouldMapToCommentEntity() {
        // Given
        final var content = "test";
        final var mix = Instancio.create(Mix.class);
        final var user = Instancio.create(User.class);

        // When
        final var result = mapper.toCommentEntity(content, user, mix);

        // Then
        assertThat(result.getMix()).isEqualTo(mix);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getNumberOfReplies()).isEqualTo(0);
        assertThat(result.getIdentifier()).isNotBlank();
        assertThat(result.getLikes()).isEmpty();

        assertThat(result.getId()).isNull();
        assertThat(result.getCreatedAt()).isNull();
        assertThat(result.getUpdatedAt()).isNull();
        assertThat(result.getParentComment()).isNull();
    }
}