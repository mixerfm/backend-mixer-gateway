package fm.mixer.gateway.module.player.mapper;

import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.model.UserReaction;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.player.api.v1.model.Track;
import fm.mixer.gateway.module.player.persistance.entity.MixTrack;
import fm.mixer.gateway.module.player.persistance.entity.MixTrackLike;
import fm.mixer.gateway.module.player.persistance.entity.PlaySession;
import fm.mixer.gateway.module.react.persistance.entity.model.ReactionType;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.test.UnitTest;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.mockStatic;

@UnitTest
class PlayerMapperUnitTest {

    private final PlayerMapper mapper = Mappers.getMapper(PlayerMapper.class);

    @Test
    void shouldMapToSession() {
        // Given
        final var user = Instancio.create(User.class);
        final var playSession = Instancio.of(PlaySession.class)
            .set(field(PlaySession::getTrack), createTrack(user))
            .create();

        try (final var userPrincipal = mockStatic(UserPrincipalUtil.class)) {
            userPrincipal.when(UserPrincipalUtil::getCurrentActiveUser).thenReturn(Optional.of(user));

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
        final var user = Instancio.create(User.class);
        final var track = createTrack(user);

        try (final var userPrincipal = mockStatic(UserPrincipalUtil.class)) {
            userPrincipal.when(UserPrincipalUtil::getCurrentActiveUser).thenReturn(Optional.of(user));

            // When
            final var result = mapper.toTrackListObject(List.of(track));

            // Then
            assertThat(result.getTracks()).hasSize(1);
            assertTrack(result.getTracks().getFirst(), track);
        }
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

    static MixTrack createTrack(User user) {
        return Instancio.of(MixTrack.class)
            .set(field(MixTrack::getReactions), Set.of(
                createTrackReaction(user, ReactionType.LIKE),
                createTrackReaction(user, ReactionType.RECOMMEND)
            ))
            .create();
    }

    static MixTrackLike createTrackReaction(User user, ReactionType type) {
        return Instancio.of(MixTrackLike.class)
            .set(field(MixTrackLike::getType), type)
            .set(field(MixTrackLike::getValue), true)
            .set(field(MixTrackLike::getUser), user)
            .create();
    }
}