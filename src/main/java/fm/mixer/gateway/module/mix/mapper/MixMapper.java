package fm.mixer.gateway.module.mix.mapper;

import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.common.mapper.PaginatedMapping;
import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.module.mix.api.v1.model.Artist;
import fm.mixer.gateway.module.mix.api.v1.model.Author;
import fm.mixer.gateway.module.mix.api.v1.model.CollectionList;
import fm.mixer.gateway.module.mix.api.v1.model.MixType;
import fm.mixer.gateway.module.mix.api.v1.model.SingleCollection;
import fm.mixer.gateway.module.mix.api.v1.model.SingleMix;
import fm.mixer.gateway.module.mix.api.v1.model.UserLikedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserLikedMixesMixesInner;
import fm.mixer.gateway.module.mix.api.v1.model.UserListenedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserListenedMixesMixesInner;
import fm.mixer.gateway.module.mix.api.v1.model.UserUploadedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserUploadedMixesMixesInner;
import fm.mixer.gateway.module.mix.api.v1.model.Visibility;
import fm.mixer.gateway.module.mix.config.MixTypeConfig;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.mix.persistance.entity.MixCollection;
import fm.mixer.gateway.module.mix.persistance.entity.MixLike;
import fm.mixer.gateway.module.mix.persistance.entity.MixTag;
import fm.mixer.gateway.module.mix.persistance.entity.MixTrack;
import fm.mixer.gateway.module.mix.persistance.entity.model.VisibilityType;
import fm.mixer.gateway.module.player.persistance.entity.PlaySession;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.entity.UserArtist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;
import org.springframework.data.domain.Page;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Mapper(imports = {PaginationMapper.class, LocalDateTime.class})
public interface MixMapper {

    @Named("toMix")
    @MixCommonMapping
    fm.mixer.gateway.module.mix.api.v1.model.Mix toMix(Mix mix);

    @Named("toLiked")
    default boolean toLiked(Mix mix) {
        final var currentActiveUser = UserPrincipalUtil.getCurrentActiveUser();

        return currentActiveUser.filter(user -> mix.getLikes().stream()
                .anyMatch(like -> user.getId().equals(like.getUser().getId()) && Boolean.TRUE.equals(like.getLiked()))
            )
            .isPresent();
    }

    @Named("toType")
    default MixType toType(Mix mix) {
        return MixTypeConfig.getConfig().entrySet().stream()
            .filter(entry -> mix.getPlayCount() >= entry.getValue().getNumberOfLikes())
            .max(Comparator.comparing(a -> a.getValue().getNumberOfLikes()))
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    default List<String> toTagList(Set<MixTag> tags) {
        return Objects.isNull(tags) ? List.of() : tags.stream().map(MixTag::getName).toList();
    }

    @MixCommonMapping
    SingleMix toSingleMix(Mix mix);

    default String toDuration(List<MixTrack> tracks) {
        return tracks.stream()
            .map(MixTrack::getDuration)
            .reduce(Duration::plus)
            .orElse(Duration.ZERO)
            .toString();
    }

    @ValueMapping(target = "PUBLIC", source = "PUBLIC")
    @ValueMapping(target = "PRIVATE", source = "PRIVATE")
    @ValueMapping(target = "PREMIUM", source = "PREMIUM")
    Visibility toVisibility(VisibilityType visibility);

    @Mapping(target = "username", source = "identifier")
    @Mapping(target = "displayName", source = "name")
    @Mapping(target = "avatarUrl", source = "avatar")
    Author toAuthor(User user);

    @Mapping(target = "username", source = "identifier")
    @Mapping(target = "displayName", source = "name")
    Artist toArtist(UserArtist userArtist);

    @PaginatedMapping
    @Mapping(target = "mixes", source = "items.content")
    UserLikedMixes toUserLikedMixes(Page<MixLike> items, PaginationRequest paginationRequest);

    default UserLikedMixesMixesInner toUserLikedMixesMixesInner(MixLike mixLike) {
        return Optional.ofNullable(toUserLikedMixesMixesInner(mixLike.getMix()))
            .map(liked -> liked.likedDateTime(mixLike.getUpdatedAt()))
            .orElse(null);
    }

    @MixCommonMapping
    @Mapping(target = "likedDateTime", ignore = true)
    UserLikedMixesMixesInner toUserLikedMixesMixesInner(Mix mix);

    @PaginatedMapping
    @Mapping(target = "mixes", source = "items.content")
    UserUploadedMixes toUserUploadedMixes(Page<Mix> items, PaginationRequest paginationRequest);

    @MixCommonMapping
    @Mapping(target = "uploadedDateTime", source = "createdAt")
    UserUploadedMixesMixesInner toUserUploadedMixesMixesInner(Mix mix);

    @PaginatedMapping
    @Mapping(target = "mixes", source = "items.content")
    UserListenedMixes toUserListenedMixes(Page<PlaySession> items, PaginationRequest paginationRequest);

    default UserListenedMixesMixesInner toUserListenedMixesMixesInner(PlaySession playSession) {
        return Optional.ofNullable(toUserListenedMixesMixesInner(playSession.getMix()))
            .map(listened -> listened.listenedDateTime(playSession.getCreatedAt()))
            .orElse(null);
    }

    @MixCommonMapping
    @Mapping(target = "listenedDateTime", ignore = true)
    UserListenedMixesMixesInner toUserListenedMixesMixesInner(Mix mix);

    @PaginatedMapping
    @Mapping(target = "collections", source = "items.content")
    CollectionList mapToCollectionList(Page<MixCollection> items, PaginationRequest paginationRequest);

    @MixCollectionCommonMapping
    @Mapping(target = "mixes", source = "mixes", qualifiedByName = "toMix")
    SingleCollection mapToSingleCollection(MixCollection collection);

    // MapStruct wrongly set variables if all are not stated (Bug in library)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "mix", source = "mix")
    @Mapping(target = "liked", source = "liked")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MixLike toMixLike(User user, Mix mix, Boolean liked);
}
