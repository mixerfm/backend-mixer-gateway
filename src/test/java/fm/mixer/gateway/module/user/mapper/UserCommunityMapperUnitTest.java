package fm.mixer.gateway.module.user.mapper;

import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.model.PaginationMetadata;
import fm.mixer.gateway.module.user.api.v1.model.CompactUser;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.entity.UserFollower;
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
class UserCommunityMapperUnitTest {

    final UserCommunityMapper mapper = Mappers.getMapper(UserCommunityMapper.class);

    @Test
    void shouldMapToUserFollowingList() {
        // Given
        final var userFollower = Instancio.create(UserFollower.class);
        final var paginationRequest = PaginationMapper.toPaginationRequest(1, 1, List.of());

        try (final var currentUser = mockStatic(UserPrincipalUtil.class)) {
            currentUser.when(UserPrincipalUtil::getCurrentActiveUser).thenReturn(Optional.empty());

            // When
            final var result = mapper.toUserFollowingList(new PageImpl<>(List.of(userFollower)), paginationRequest);

            // Then
            assertThat(result.getUsers()).hasSize(1).allSatisfy(user -> assertFollower(user, userFollower.getFollowsUser()));
            assertPaginationMetadata(result.getMetadata());
        }
    }

    @Test
    void shouldMapToUserFollowerList() {
        // Given
        final var userFollower = Instancio.create(UserFollower.class);
        final var paginationRequest = PaginationMapper.toPaginationRequest(1, 1, List.of());

        try (final var currentUser = mockStatic(UserPrincipalUtil.class)) {
            currentUser.when(UserPrincipalUtil::getCurrentActiveUser).thenReturn(Optional.empty());

            // When
            final var result = mapper.toUserFollowerList(new PageImpl<>(List.of(userFollower)), paginationRequest);

            // Then
            assertThat(result.getUsers()).hasSize(1).allSatisfy(user -> assertFollower(user, userFollower.getUser()));
            assertPaginationMetadata(result.getMetadata());
        }
    }

    @Test
    void shouldMapToUserFollower() {
        // Given
        final var user = Instancio.create(User.class);
        final var followsUser = Instancio.create(User.class);

        // When
        final var result = mapper.toUserFollower(user, followsUser);

        // Then
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getFollowsUser()).isEqualTo(followsUser);

        assertThat(result.getId()).isNull();
        assertThat(result.getCreatedAt()).isNull();
    }

    private void assertFollower(CompactUser user, User expected) {
        assertThat(user.getActive()).isEqualTo(expected.getActive());
        assertThat(user.getDisplayName()).isEqualTo(expected.getName());
        assertThat(user.getAvatarUrl()).isEqualTo(expected.getAvatar());
        assertThat(user.getUsername()).isEqualTo(expected.getIdentifier());
        assertThat(user.getProfileColor()).isEqualTo(expected.getProfileColor());
        assertThat(user.getRelation()).isNull();
    }

    private void assertPaginationMetadata(PaginationMetadata paginationMetadata) {
        assertThat(paginationMetadata.getCurrentPage()).isEqualTo(1);
        assertThat(paginationMetadata.getTotalItems()).isEqualTo(1);
        assertThat(paginationMetadata.getTotalPages()).isEqualTo(1);

        assertThat(paginationMetadata.getPreviousPage()).isNull();
        assertThat(paginationMetadata.getNextPage()).isNull();
    }
}