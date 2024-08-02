package fm.mixer.gateway.module.mix.mapper;

import fm.mixer.gateway.common.mapper.CreatorCommonMapping;
import fm.mixer.gateway.common.mapper.PaginatedMapping;
import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.common.model.SortField;
import fm.mixer.gateway.module.mix.api.v1.model.CollectionList;
import fm.mixer.gateway.module.mix.api.v1.model.Creator;
import fm.mixer.gateway.module.mix.api.v1.model.MixList;
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
import fm.mixer.gateway.module.mix.persistance.entity.MixCollectionRelation;
import fm.mixer.gateway.module.mix.persistance.entity.MixCollectionRelation_;
import fm.mixer.gateway.module.mix.persistance.entity.MixCollection_;
import fm.mixer.gateway.module.mix.persistance.entity.MixLike;
import fm.mixer.gateway.module.mix.persistance.entity.MixLike_;
import fm.mixer.gateway.module.mix.persistance.entity.MixTag;
import fm.mixer.gateway.module.mix.persistance.entity.Mix_;
import fm.mixer.gateway.module.mix.persistance.entity.model.VisibilityType;
import fm.mixer.gateway.module.player.persistance.entity.PlaySession;
import fm.mixer.gateway.module.react.persistance.mapper.ReactionMapper;
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

@Mapper(imports = {PaginationMapper.class, LocalDateTime.class, ReactionMapper.class})
public interface MixMapper {

    @Named("toMix")
    @MixCommonMapping
    fm.mixer.gateway.module.mix.api.v1.model.Mix toMix(Mix mix);

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
    @Mapping(target = "profileColor", ignore = true)
    Creator toArtist(UserArtist userArtist);

    @PaginatedMapping
    @Mapping(target = "mixes", source = "items.content")
    UserLikedMixes toUserLikedMixes(Page<MixLike> items, PaginationRequest paginationRequest);

    default UserLikedMixesMixesInner toUserLikedMixesMixesInner(MixLike mixLike) {
        return Optional.ofNullable(toUserLikedMixesMixesInner(mixLike.getItem()))
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
    CollectionList toCollectionList(Page<MixCollection> items, PaginationRequest paginationRequest);

    @MixCollectionCommonMapping
    @Mapping(target = "mixes", ignore = true)
    SingleCollection toSingleCollection(MixCollection collection);

    default fm.mixer.gateway.module.mix.api.v1.model.Mix toMixList(MixCollectionRelation relation) {
        return toMix(relation.getMix());
    }

    @PaginatedMapping
    @Mapping(target = "mixes", source = "items.content")
    MixList toMixList(Page<MixCollectionRelation> items, PaginationRequest paginationRequest);

    default List<fm.mixer.gateway.module.mix.api.v1.model.Mix> toMixList(List<Mix> mixes) {
        return Objects.isNull(mixes) ? List.of() : mixes.stream().map(this::toMix).toList();
    }

    @PaginatedMapping
    @Named("toMixListSearchResult")
    @Mapping(target = "mixes", source = "items.content")
    MixList toMixListSearchResult(Page<Mix> items, PaginationRequest paginationRequest);

    static Map<SortField, String> toMixColumnMapping() {
        return Map.of(
            SortField.NAME, Mix_.NAME,
            SortField.DATE, Mix_.CREATED_AT,
            SortField.POPULARITY, Mix_.PLAY_COUNT
            // SortField.TREND - popularity by day
        );
    }

    default Map<SortField, String> toMixLikeColumnMapping() {
        return Map.of(
            SortField.NAME, String.join(".", MixLike_.ITEM, Mix_.NAME),
            SortField.DATE, MixLike_.UPDATED_AT,
            SortField.POPULARITY, String.join(".", MixLike_.ITEM, Mix_.PLAY_COUNT)
        );
    }

    static Map<SortField, String> toCollectionColumnMapping() {
        return Map.of(
            SortField.NAME, MixCollection_.NAME,
            SortField.DATE, MixCollection_.CREATED_AT,
            SortField.POPULARITY, MixCollection_.NUMBER_OF_REACTIONS
            // SortField.TREND - popularity by day
        );
    }

    default Map<SortField, String> toCollectionMixColumnMapping() {
        return Map.of(
            SortField.NAME, String.join(".", MixCollectionRelation_.MIX, Mix_.NAME),
            SortField.DATE, String.join(".", MixCollectionRelation_.MIX, MixCollection_.CREATED_AT),
            SortField.POPULARITY, String.join(".", MixCollectionRelation_.MIX, MixCollection_.NUMBER_OF_REACTIONS)
            // SortField.TREND - popularity by day
        );
    }
}
