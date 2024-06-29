package fm.mixer.gateway.module.player.mapper;

import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.common.mapper.CreatorCommonMapping;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.player.api.v1.model.Album;
import fm.mixer.gateway.module.player.api.v1.model.Creator;
import fm.mixer.gateway.module.player.api.v1.model.Session;
import fm.mixer.gateway.module.player.api.v1.model.Track;
import fm.mixer.gateway.module.player.api.v1.model.TrackList;
import fm.mixer.gateway.module.player.api.v1.model.UserReaction;
import fm.mixer.gateway.module.player.persistance.entity.MixAlbum;
import fm.mixer.gateway.module.player.persistance.entity.MixTrack;
import fm.mixer.gateway.module.player.persistance.entity.MixTrackLike;
import fm.mixer.gateway.module.player.persistance.entity.PlaySession;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.entity.UserArtist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(imports = {Duration.class})
public interface PlayerMapper {

    String TRACK_DELIMITER = ",";

    @Mapping(target = "mixId", source = "mix.identifier")
    @Mapping(target = "device", ignore = true)
    Session toSession(PlaySession playSession);

    default TrackList toTrackListObject(List<MixTrack> trackList) {
        return new TrackList().tracks(toTrackList(trackList));
    }

    List<Track> toTrackList(List<MixTrack> trackList);

    @Mapping(target = "artists", source = "artist")
    @Mapping(target = "reactions", source = "likes")
    Track toTrack(MixTrack mixTrack);

    default List<UserReaction> toReactions(final Set<MixTrackLike> likes) {
        final var reaction = UserPrincipalUtil.getCurrentActiveUser()
            .flatMap(user -> likes.stream().filter(like -> user.getId().equals(like.getUser().getId())).findFirst());

        if (reaction.isEmpty()) {
            return List.of();
        }

        final var reactions = new ArrayList<UserReaction.TypeEnum>();

        if (Objects.nonNull(reaction.get().getLiked())) {
            reactions.add(reaction.get().getLiked() ? UserReaction.TypeEnum.LIKE : UserReaction.TypeEnum.DISLIKE);
        }
        if (Objects.nonNull(reaction.get().getRecommend())) {
            reactions.add(reaction.get().getRecommend() ? UserReaction.TypeEnum.RECOMMEND : UserReaction.TypeEnum.DO_NOT_RECOMMEND);
        }

        return reactions.stream().map(UserReaction::new).toList();
    }

    default List<Creator> toArtistList(UserArtist userArtist) {
        return List.of(toArtist(userArtist));
    }

    @CreatorCommonMapping
    Creator toArtist(UserArtist userArtist);

    @Mapping(target = "displayName", source = "name")
    Album toAlbum(MixAlbum mixAlbum);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "track", source = "track")
    @Mapping(target = "liked", expression = "java(toLiked(reaction).orElse(null))")
    @Mapping(target = "recommend", expression = "java(toRecommend(reaction).orElse(null))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MixTrackLike toMixTrackLikeEntity(User user, MixTrack track, UserReaction.TypeEnum reaction);

    default Optional<Boolean> toLiked(UserReaction.TypeEnum reaction) {
        if (UserReaction.TypeEnum.LIKE.equals(reaction)) {
            return Optional.of(Boolean.TRUE);
        }
        if (UserReaction.TypeEnum.DISLIKE.equals(reaction)) {
            return Optional.of(Boolean.FALSE);
        }
        return Optional.empty();
    }

    default Optional<Boolean> toRecommend(UserReaction.TypeEnum reaction) {
        if (UserReaction.TypeEnum.RECOMMEND.equals(reaction)) {
            return Optional.of(Boolean.TRUE);
        }
        if (UserReaction.TypeEnum.DO_NOT_RECOMMEND.equals(reaction)) {
            return Optional.of(Boolean.FALSE);
        }
        return Optional.empty();
    }

    @Mapping(target = "user", source = "user")
    @Mapping(target = "mix", source = "mix")
    @Mapping(target = "track", source = "track")
    @Mapping(target = "duration", expression = "java(Duration.ZERO)")
    @Mapping(target = "shuffle", constant = "false")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tracks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void toPlaySessionEntity(@MappingTarget PlaySession playSession, User user, Mix mix, MixTrack track);

    default String toMixTracksString(List<MixTrack> tracks) {
        return tracks.stream().map(MixTrack::getId).map(Object::toString).collect(Collectors.joining(TRACK_DELIMITER));
    }
}
