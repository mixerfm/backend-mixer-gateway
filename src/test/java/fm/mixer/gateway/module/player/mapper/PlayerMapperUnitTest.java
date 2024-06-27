package fm.mixer.gateway.module.player.mapper;

import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.player.api.v1.model.Track;
import fm.mixer.gateway.module.player.api.v1.model.UserReaction;
import fm.mixer.gateway.module.player.persistance.entity.MixTrack;
import fm.mixer.gateway.module.player.persistance.entity.PlaySession;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class PlayerMapperUnitTest {

    final PlayerMapper mapper = Mappers.getMapper(PlayerMapper.class);

    @Test
    void shouldMapToSession() {
        // Given
        final var playSession = Instancio.create(PlaySession.class);
        final var like = playSession.getTrack().getLikes().stream().findFirst().orElseThrow();
        like.setRecommend(true);
        like.setLiked(true);

        try (final var user = mockStatic(UserPrincipalUtil.class)) {
            user.when(UserPrincipalUtil::getCurrentActiveUser).thenReturn(Optional.of(like.getUser()));

            // When
            final var result = mapper.toSession(playSession);

            // Then
            assertThat(result.getMixId()).isEqualTo(playSession.getMix().getIdentifier());
            assertThat(result.getDevice()).isNull();
            assertTrack(result.getTrack(), playSession.getTrack());
        }
    }

    @Test
    void shouldMapToTrackListObject() {
        // Given
        final var track = Instancio.create(MixTrack.class);
        final var like = track.getLikes().stream().findFirst().orElseThrow();
        like.setRecommend(true);
        like.setLiked(true);

        try (final var user = mockStatic(UserPrincipalUtil.class)) {
            user.when(UserPrincipalUtil::getCurrentActiveUser).thenReturn(Optional.of(like.getUser()));

            // When
            final var result = mapper.toTrackListObject(List.of(track));

            // Then
            assertThat(result.getTracks()).hasSize(1);
            assertTrack(result.getTracks().getFirst(), track);
        }
    }

    @Test
    void shouldMapToMixTrackLikeEntity() {
        // Given
        final var user = Instancio.create(User.class);
        final var track = Instancio.create(MixTrack.class);

        // When
        final var result = mapper.toMixTrackLikeEntity(user, track, UserReaction.TypeEnum.RECOMMEND);

        // Then
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getTrack()).isEqualTo(track);
        assertThat(result.getLiked()).isNull();
        assertThat(result.getRecommend()).isTrue();
        assertThat(result.getId()).isNull();
        assertThat(result.getUpdatedAt()).isNull();
    }

    @Test
    void shouldMapToPlaySessionEntity() {
        // Given
        final var session = new PlaySession();

        final var track = Instancio.create(MixTrack.class);
        final var user = Instancio.create(User.class);
        final var mix = Instancio.create(Mix.class);

        // When
        mapper.toPlaySessionEntity(session, user, mix, track);

        // Then
        assertThat(session.getUser()).isEqualTo(user);
        assertThat(session.getMix()).isEqualTo(mix);
        assertThat(session.getTrack()).isEqualTo(track);
        assertThat(session.getId()).isNull();
        assertThat(session.getCreatedAt()).isNull();
        assertThat(session.getUpdatedAt()).isNull();
    }

    @ParameterizedTest
    @EnumSource(UserReaction.TypeEnum.class)
    void shouldMapToLiked(UserReaction.TypeEnum reaction) {
        // When
        final var result = mapper.toLiked(reaction);

        // Then
        asserReaction(reaction, result.orElse(null), UserReaction.TypeEnum.LIKE, UserReaction.TypeEnum.DISLIKE);
    }

    @ParameterizedTest
    @EnumSource(UserReaction.TypeEnum.class)
    void shouldMapToRecommend(UserReaction.TypeEnum reaction) {
        // When
        final var result = mapper.toRecommend(reaction);

        // Then
        asserReaction(reaction, result.orElse(null), UserReaction.TypeEnum.RECOMMEND, UserReaction.TypeEnum.DO_NOT_RECOMMEND);
    }

    void assertTrack(Track result, MixTrack entity) {
        assertThat(result.getIdentifier()).isEqualTo(entity.getIdentifier());
        assertThat(result.getName()).isEqualTo(entity.getName());
        assertThat(result.getStreamUrl()).isEqualTo(entity.getStreamUrl());
        assertThat(result.getDuration()).isEqualTo(entity.getDuration().toString());

        assertThat(result.getReactions()).containsExactlyInAnyOrderElementsOf(
            Stream.of(UserReaction.TypeEnum.LIKE, UserReaction.TypeEnum.RECOMMEND).map(UserReaction::new).toList()
        );

        // Album
        assertThat(result.getAlbum().getIdentifier()).isEqualTo(entity.getAlbum().getIdentifier());
        assertThat(result.getAlbum().getDisplayName()).isEqualTo(entity.getAlbum().getName());
        assertThat(result.getAlbum().getReleaseDate()).isEqualTo(entity.getAlbum().getReleaseDate());

        // Artist
        assertThat(result.getArtists()).hasSize(1);

        final var artist = result.getArtists().getFirst();
        assertThat(artist.getUsername()).isEqualTo(entity.getArtist().getIdentifier());
        assertThat(artist.getDisplayName()).isEqualTo(entity.getArtist().getName());
        assertThat(artist.getAvatarUrl()).isEqualTo(entity.getArtist().getAvatar());
        assertThat(artist.getActive()).isEqualTo(entity.getArtist().getActive());
        assertThat(artist.getProfileColor()).isNull();

    }

    void asserReaction(UserReaction.TypeEnum given, Boolean result, UserReaction.TypeEnum positive, UserReaction.TypeEnum negative) {
        if (!List.of(positive, negative).contains(given)) {
            assertThat(result).isNull();
        }
        if (given.equals(positive)) {
            assertThat(result).isTrue();
        }
        if (given.equals(negative)) {
            assertThat(result).isFalse();
        }
    }
}