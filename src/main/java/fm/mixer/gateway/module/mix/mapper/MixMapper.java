package fm.mixer.gateway.module.mix.mapper;

import fm.mixer.gateway.common.mapper.PaginatedMapping;
import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.module.mix.api.v1.model.Author;
import fm.mixer.gateway.module.mix.api.v1.model.SingleMix;
import fm.mixer.gateway.module.mix.api.v1.model.UserLikedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserLikedMixesMixesInner;
import fm.mixer.gateway.module.mix.api.v1.model.UserListenedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserListenedMixesMixesInner;
import fm.mixer.gateway.module.mix.api.v1.model.UserUploadedMixes;
import fm.mixer.gateway.module.mix.api.v1.model.UserUploadedMixesMixesInner;
import fm.mixer.gateway.module.mix.api.v1.model.Visibility;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.mix.persistance.entity.Track;
import fm.mixer.gateway.module.mix.persistance.entity.model.VisibilityType;
import fm.mixer.gateway.module.user.persistance.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;
import org.springframework.data.domain.Page;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(imports = {PaginationMapper.class, LocalDateTime.class})
public interface MixMapper {

    @MixCommonMapping
    SingleMix toSingleMix(Mix mix);

    default String toDuration(List<Track> tracks){
        return tracks.stream()
            .map(Track::getDuration)
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
    UserLikedMixes toUserLikedMixes(Page<Mix> items, PaginationRequest paginationRequest);

    @MixCommonMapping
    @Mapping(target = "likedDateTime", expression = "java(LocalDateTime.now())")
    UserLikedMixesMixesInner toUserLikedMixesMixesInner(Mix mix);

    @PaginatedMapping
    @Mapping(target = "mixes", source = "items.content")
    UserUploadedMixes toUserUploadedMixes(Page<Mix> items, PaginationRequest paginationRequest);

    @MixCommonMapping
    @Mapping(target = "uploadedDateTime", expression = "java(LocalDateTime.now())")
    UserUploadedMixesMixesInner toUserUploadedMixesMixesInner(Mix mix);

    @PaginatedMapping
    @Mapping(target = "mixes", source = "items.content")
    UserListenedMixes toUserListenedMixes(Page<Mix> items, PaginationRequest paginationRequest);

    @MixCommonMapping
    @Mapping(target = "listenedDateTime", expression = "java(LocalDateTime.now())")
    UserListenedMixesMixesInner toUserListenedMixesMixesInner(Mix mix);
}
