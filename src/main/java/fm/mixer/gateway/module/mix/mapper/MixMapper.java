package fm.mixer.gateway.module.mix.mapper;

import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.common.mapper.CreatorCommonMapping;
import fm.mixer.gateway.common.mapper.PaginatedMapping;
import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.module.mix.api.v1.model.CollectionList;
import fm.mixer.gateway.module.mix.api.v1.model.Creator;
import fm.mixer.gateway.module.mix.api.v1.model.MixType;
import fm.mixer.gateway.module.mix.api.v1.model.SingleCollection;
import fm.mixer.gateway.module.mix.api.v1.model.SingleMix;
import fm.mixer.gateway.module.mix.api.v1.model.UserLikedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserLikedMixesMixesInner;
import fm.mixer.gateway.module.mix.api.v1.model.UserListenedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserListenedMixesMixesInner;
import fm.mixer.gateway.module.mix.api.v1.model.UserReaction;
import fm.mixer.gateway.module.mix.api.v1.model.UserUploadedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserUploadedMixesMixesInner;
import fm.mixer.gateway.module.mix.api.v1.model.Visibility;
import fm.mixer.gateway.module.mix.config.MixTypeConfig;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.mix.persistance.entity.MixCollection;
import fm.mixer.gateway.module.mix.persistance.entity.MixLike;
import fm.mixer.gateway.module.mix.persistance.entity.MixTag;
import fm.mixer.gateway.module.mix.persistance.entity.model.VisibilityType;
import fm.mixer.gateway.module.player.persistance.entity.PlaySession;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.entity.UserArtist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;
import org.springframework.data.domain.Page;

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

    default List<UserReaction> toReactions(final Set<MixLike> likes) {
        final var currentActiveUser = UserPrincipalUtil.getCurrentActiveUser();

        return currentActiveUser.map(user -> likes.stream()
                .filter(like -> user.getId().equals(like.getUser().getId()))
                .map(like -> like.getLiked() ? UserReaction.TypeEnum.LIKE : UserReaction.TypeEnum.DISLIKE)
                .map(UserReaction::new)
                .toList()
            )
            .orElse(List.of());
    }

    @Named("toType")
    default MixType toType(Mix mix) {
        return MixTypeConfig.getConfig().entrySet().stream()
            .filter(entry -> mix.getPlayCount() >= entry.getValue().getNumberOfLikes())
            .max(Comparator.comparing(a -> a.getValue().getNumberOfLikes()))
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    default List<String> toTagList(List<MixTag> tags) {
        return Objects.isNull(tags) ? List.of() : tags.stream().map(MixTag::getName).toList();
    }

    @MixCommonMapping
    SingleMix toSingleMix(Mix mix);

    @ValueMapping(target = "PUBLIC", source = "PUBLIC")
    @ValueMapping(target = "PRIVATE", source = "PRIVATE")
    @ValueMapping(target = "PREMIUM", source = "PREMIUM")
    Visibility toVisibility(VisibilityType visibility);

    @CreatorCommonMapping
    Creator toAuthor(User user);

    @CreatorCommonMapping
    Creator toArtist(UserArtist userArtist);

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

    @Mapping(target = "user", source = "user")
    @Mapping(target = "mix", source = "mix")
    @Mapping(target = "liked", source = "liked")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MixLike toMixLikeEntity(User user, Mix mix, Boolean liked);
}
