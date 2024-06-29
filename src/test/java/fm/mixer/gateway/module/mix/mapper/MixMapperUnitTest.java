package fm.mixer.gateway.module.mix.mapper;

import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.model.PaginationMetadata;
import fm.mixer.gateway.module.mix.api.v1.model.Collection;
import fm.mixer.gateway.module.mix.api.v1.model.Creator;
import fm.mixer.gateway.module.mix.api.v1.model.UserReaction;
import fm.mixer.gateway.module.mix.config.MixTypeConfig;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.mix.persistance.entity.MixCollection;
import fm.mixer.gateway.module.mix.persistance.entity.MixLike;
import fm.mixer.gateway.module.mix.persistance.entity.MixTag;
import fm.mixer.gateway.module.mix.persistance.entity.model.VisibilityType;
import fm.mixer.gateway.module.player.persistance.entity.PlaySession;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.entity.UserArtist;
import fm.mixer.gateway.test.UnitTest;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.mockStatic;

@UnitTest
class MixMapperUnitTest {

    private final MixMapper mapper = Mappers.getMapper(MixMapper.class);

    @Test
    void shouldMapToMix() {
        // Given
        final var mix = Instancio.of(Mix.class)
            .set(field(Mix::getArtists), List.of(Instancio.create(UserArtist.class)))
            .create();
        final var mixLike = mix.getLikes().stream().findFirst().orElseThrow();
        mixLike.setLiked(true);

        try (final var user = mockStatic(UserPrincipalUtil.class)) {
            try (final var config = mockStatic(MixTypeConfig.class)) {
                config.when(MixTypeConfig::getConfig).thenReturn(Map.of());
                user.when(UserPrincipalUtil::getCurrentActiveUser).thenReturn(Optional.of(mixLike.getUser()));

                // When
                final var result = mapper.toMix(mix);

                // Then
                assertMix(mix, result);
            }
        }
    }

    @Test
    void shouldMapToSingleMix() {
        // Given
        final var mix = Instancio.of(Mix.class)
            .set(field(Mix::getArtists), List.of(Instancio.create(UserArtist.class)))
            .create();
        final var mixLike = mix.getLikes().stream().findFirst().orElseThrow();
        mixLike.setLiked(true);

        try (final var user = mockStatic(UserPrincipalUtil.class)) {
            try (final var config = mockStatic(MixTypeConfig.class)) {
                config.when(MixTypeConfig::getConfig).thenReturn(Map.of());
                user.when(UserPrincipalUtil::getCurrentActiveUser).thenReturn(Optional.of(mixLike.getUser()));

                // When
                final var result = mapper.toSingleMix(mix);

                // Then
                assertMix(mix, result);
                assertThat(result.getDescription()).isEqualTo(mix.getDescription());
            }
        }
    }

    @ParameterizedTest
    @EnumSource(VisibilityType.class)
    void shouldMapToVisibility(VisibilityType given) {
        // When
        final var result = mapper.toVisibility(given);

        // Then
        assertThat(result.name()).isEqualTo(given.name());
    }

    @Test
    void shouldMapToUserLikedMixes() {
        // Given
        // Instancio wont generate second level HashSet - bug in lib?
        final var mixLike = Instancio.of(MixLike.class).set(field(MixLike::getMix), Instancio.create(Mix.class)).create();

        mixLike.getMix().getLikes().stream().findFirst().ifPresent(l -> l.setLiked(true));

        try (final var user = mockStatic(UserPrincipalUtil.class)) {
            try (final var config = mockStatic(MixTypeConfig.class)) {
                config.when(MixTypeConfig::getConfig).thenReturn(Map.of());
                user.when(UserPrincipalUtil::getCurrentActiveUser).thenReturn(
                    mixLike.getMix().getLikes().stream().findFirst().map(MixLike::getUser)
                );

                // When
                final var result = mapper.toUserLikedMixes(
                    new PageImpl<>(List.of(mixLike)),
                    PaginationMapper.toPaginationRequest(1, 1, List.of())
                );

                // Then
                assertMix(mixLike.getMix(), result.getMixes().getFirst());
                assertPaginationMetadata(result.getMetadata());
            }
        }
    }

    @Test
    void shouldMapToUserUploadedMixes() {
        // Given
        final var mix = Instancio.create(Mix.class);

        mix.getLikes().stream().findFirst().ifPresent(l -> l.setLiked(true));

        try (final var user = mockStatic(UserPrincipalUtil.class)) {
            try (final var config = mockStatic(MixTypeConfig.class)) {
                config.when(MixTypeConfig::getConfig).thenReturn(Map.of());
                user.when(UserPrincipalUtil::getCurrentActiveUser).thenReturn(
                    mix.getLikes().stream().findFirst().map(MixLike::getUser)
                );

                // When
                final var result = mapper.toUserUploadedMixes(
                    new PageImpl<>(List.of(mix)),
                    PaginationMapper.toPaginationRequest(1, 1, List.of())
                );

                // Then
                assertMix(mix, result.getMixes().getFirst());
                assertPaginationMetadata(result.getMetadata());
            }
        }
    }

    @Test
    void shouldMapToUserListenedMixes() {
        // Given
        final var playSession = Instancio.create(PlaySession.class);

        playSession.getMix().getLikes().stream().findFirst().ifPresent(l -> l.setLiked(true));

        try (final var user = mockStatic(UserPrincipalUtil.class)) {
            try (final var config = mockStatic(MixTypeConfig.class)) {
                config.when(MixTypeConfig::getConfig).thenReturn(Map.of());
                user.when(UserPrincipalUtil::getCurrentActiveUser).thenReturn(
                    playSession.getMix().getLikes().stream().findFirst().map(MixLike::getUser)
                );

                // When
                final var result = mapper.toUserListenedMixes(
                    new PageImpl<>(List.of(playSession)),
                    PaginationMapper.toPaginationRequest(1, 1, List.of())
                );

                // Then
                assertMix(playSession.getMix(), result.getMixes().getFirst());
                assertPaginationMetadata(result.getMetadata());
            }
        }
    }

    @Test
    void shouldMapToCollectionList() {
        // Given
        final var collection = Instancio.create(MixCollection.class);
        final var items = new PageImpl<>(List.of(collection));
        final var paginationRequest = PaginationMapper.toPaginationRequest(1, 1, List.of());

        // When
        final var result = mapper.toCollectionList(items, paginationRequest);

        // Then
        assertThat(result.getCollections()).hasSize(1).allSatisfy(c -> assertCollection(collection, c));
        assertPaginationMetadata(result.getMetadata());
    }

    @Test
    void shouldMapToSingleCollection() {
        // Given
        final var collection = Instancio.create(MixCollection.class);

        // When
        final var result = mapper.toSingleCollection(collection);

        // Then
        assertCollection(collection, result);
        assertThat(result.getDescription()).isEqualTo(collection.getDescription());
    }

    @Test
    void shouldMapToMixLikeEntity() {
        // Given
        final var user = Instancio.create(User.class);
        final var mix = Instancio.create(Mix.class);

        // When
        final var result = mapper.toMixLikeEntity(user, mix, true);

        // Then
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getMix()).isEqualTo(mix);
        assertThat(result.getLiked()).isTrue();

        assertThat(result.getId()).isNull();
        assertThat(result.getUpdatedAt()).isNull();
    }

    private void assertMix(Mix entity, fm.mixer.gateway.module.mix.api.v1.model.Mix result) {
        assertThat(result.getIdentifier()).isEqualTo(entity.getIdentifier());
        assertThat(result.getName()).isEqualTo(entity.getName());
        assertThat(result.getAvatarUrl()).isEqualTo(entity.getAvatar());
        assertThat(result.getNumberOfPlays()).isEqualTo(entity.getPlayCount());
        assertThat(result.getNumberOfLikes()).isEqualTo(entity.getLikes().size());
        assertThat(result.getNumberOfTracks()).isEqualTo(entity.getNumberOfTracks());
        assertThat(result.getDuration()).isEqualTo(entity.getDuration().toString());
        assertThat(result.getVisibility().name()).isEqualTo(entity.getVisibility().name());
        assertThat(result.getReactions()).hasSize(1).allMatch(reaction -> UserReaction.TypeEnum.LIKE.equals(reaction.getType()));
        assertThat(result.getTags()).isEqualTo(entity.getTags().stream().map(MixTag::getName).map(Objects::toString).toList());
        assertThat(result.getNsfw()).isEqualTo(entity.isNsfw());

        assertArtist(result.getArtists().stream().findFirst().orElseThrow(), entity.getArtists().stream().findFirst().orElseThrow());
        assertUser(result.getAuthor(), entity.getUser());

        assertThat(result.getType()).isNull();
    }

    private void assertCollection(MixCollection entity, Collection result) {
        assertThat(result.getIdentifier()).isEqualTo(entity.getIdentifier());
        assertThat(result.getName()).isEqualTo(entity.getName());
        assertThat(result.getAvatarUrl()).isEqualTo(entity.getAvatar());
        assertThat(result.getVisibility().name()).isEqualTo(entity.getVisibility().name());
        assertThat(result.getTags()).isEqualTo(entity.getTags().stream().map(MixTag::getName).toList());
        assertThat(result.getMixes()).isNull(); // This is set in service after mapping is done

        assertThat(result.getArtists()).isEmpty();
        assertThat(result.getReactions()).isEmpty();

        assertUser(result.getAuthor(), entity.getUser());
    }

    private void assertPaginationMetadata(PaginationMetadata paginationMetadata) {
        assertThat(paginationMetadata.getCurrentPage()).isEqualTo(1);
        assertThat(paginationMetadata.getTotalItems()).isEqualTo(1);
        assertThat(paginationMetadata.getTotalPages()).isEqualTo(1);

        assertThat(paginationMetadata.getPreviousPage()).isNull();
        assertThat(paginationMetadata.getNextPage()).isNull();
    }

    private void assertUser(Creator creator, User user) {
        assertThat(creator.getUsername()).isEqualTo(user.getIdentifier());
        assertThat(creator.getDisplayName()).isEqualTo(user.getName());
        assertThat(creator.getAvatarUrl()).isEqualTo(user.getAvatar());
        assertThat(creator.getProfileColor()).isEqualTo(user.getProfileColor());
        assertThat(creator.getActive()).isEqualTo(user.getActive());
    }

    private void assertArtist(Creator creator, UserArtist artist) {
        assertThat(creator.getUsername()).isEqualTo(artist.getIdentifier());
        assertThat(creator.getDisplayName()).isEqualTo(artist.getName());
        assertThat(creator.getAvatarUrl()).isEqualTo(artist.getAvatar());
        assertThat(creator.getActive()).isEqualTo(artist.getActive());
    }
}