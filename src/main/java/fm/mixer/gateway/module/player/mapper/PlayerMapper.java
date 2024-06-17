package fm.mixer.gateway.module.player.mapper;

import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.mix.persistance.entity.MixAlbum;
import fm.mixer.gateway.module.mix.persistance.entity.MixTrack;
import fm.mixer.gateway.module.mix.persistance.entity.MixTrackLike;
import fm.mixer.gateway.module.player.api.v1.model.Album;
import fm.mixer.gateway.module.player.api.v1.model.Artist;
import fm.mixer.gateway.module.player.api.v1.model.Session;
import fm.mixer.gateway.module.player.api.v1.model.Track;
import fm.mixer.gateway.module.player.api.v1.model.TrackList;
import fm.mixer.gateway.module.player.persistance.entity.PlaySession;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.entity.UserArtist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.Duration;
import java.util.List;
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
    @Mapping(target = "liked", source = ".", qualifiedByName = "toLiked")
    Track toTrack(MixTrack mixTrack);

    default List<Artist> toArtistList(UserArtist userArtist) {
        return List.of(toArtist(userArtist));
    }

    @Mapping(target = "username", source = "identifier")
    @Mapping(target = "displayName", source = "name")
    Artist toArtist(UserArtist userArtist);

    @Mapping(target = "displayName", source = "name")
    Album toAlbum(MixAlbum mixAlbum);

    @Named("toLiked")
    default Boolean toLiked(MixTrack mixTrack) {
        return false; // TODO
    }

    // MapStruct wrongly set variables if all are not stated (Bug in library)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "track", source = "track")
    @Mapping(target = "liked", source = "liked")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MixTrackLike toMixTrackLike(User user, MixTrack track, Boolean liked);

    // MapStruct wrongly set variables if all are not stated (Bug in library)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "mix", source = "mix")
    @Mapping(target = "track", source = "track")
    @Mapping(target = "duration", expression = "java(Duration.ZERO)")
    @Mapping(target = "tracks", source = "mix.tracks")
    @Mapping(target = "shuffle", constant = "false")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void toPlaySession(@MappingTarget PlaySession playSession, User user, Mix mix, MixTrack track);

    default String toMixTracksString(List<MixTrack> tracks) {
        return tracks.stream().map(MixTrack::getId).map(Object::toString).collect(Collectors.joining(TRACK_DELIMITER));
    }
}
