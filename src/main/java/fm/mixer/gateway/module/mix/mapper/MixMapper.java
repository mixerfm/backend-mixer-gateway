package fm.mixer.gateway.module.mix.mapper;

import fm.mixer.gateway.common.mapper.PaginatedMapping;
import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.module.mix.api.v1.model.Author;
import fm.mixer.gateway.module.mix.api.v1.model.CollectionList;
import fm.mixer.gateway.module.mix.api.v1.model.SingleCollection;
import fm.mixer.gateway.module.mix.api.v1.model.SingleMix;
import fm.mixer.gateway.module.mix.api.v1.model.UserLikedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserLikedMixesMixesInner;
import fm.mixer.gateway.module.mix.api.v1.model.UserListenedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserListenedMixesMixesInner;
import fm.mixer.gateway.module.mix.api.v1.model.UserUploadedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserUploadedMixesMixesInner;
import fm.mixer.gateway.module.mix.api.v1.model.Visibility;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.mix.persistance.entity.MixCollection;
import fm.mixer.gateway.module.mix.persistance.entity.MixLike;
import fm.mixer.gateway.module.mix.persistance.entity.MixTag;
import fm.mixer.gateway.module.mix.persistance.entity.MixTrack;
import fm.mixer.gateway.module.mix.persistance.entity.model.VisibilityType;
import fm.mixer.gateway.module.play.persistance.entity.PlaySessionHistory;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;
import org.springframework.data.domain.Page;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
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
        // TODO change mix.getUser().getId() with SecurityContextHolder id
        return mix.getLikes().stream()
            .anyMatch(like -> like.getUser().getId().equals(mix.getUser().getId()) && Boolean.TRUE.equals(like.getLiked()));
    }

    default List<String> toTagList(Set<MixTag> tags) {
        return Objects.isNull(tags) ? List.of() : tags.stream().map(MixTag::getName).toList();
    }

    @MixCommonMapping
    SingleMix toSingleMix(Mix mix);

    default String toDuration(Set<MixTrack> tracks){
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
    UserListenedMixes toUserListenedMixes(Page<PlaySessionHistory> items, PaginationRequest paginationRequest);

    default UserListenedMixesMixesInner toUserListenedMixesMixesInner(PlaySessionHistory playSessionHistory) {
        return Optional.ofNullable(toUserListenedMixesMixesInner(playSessionHistory.getMix()))
            .map(listened -> listened.listenedDateTime(playSessionHistory.getCreatedAt()))
            .orElse(null);
    }

    @MixCommonMapping
    @Mapping(target = "listenedDateTime", ignore = true)
    UserListenedMixesMixesInner toUserListenedMixesMixesInner(Mix mix);

    @PaginatedMapping
    @Mapping(target = "collections", source = "items.content")
    CollectionList mapToCollectionList(Page<MixCollection> items, PaginationRequest paginationRequest);

    @MixCollectionCommon
    @Mapping(target = "mixes", source = "mixes", qualifiedByName = "toMix")
    SingleCollection mapToSingleCollection(MixCollection collection);
}
