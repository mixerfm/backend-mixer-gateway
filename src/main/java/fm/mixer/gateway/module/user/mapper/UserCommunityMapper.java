package fm.mixer.gateway.module.user.mapper;

import fm.mixer.gateway.common.mapper.PaginatedMapping;
import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.common.model.SortField;
import fm.mixer.gateway.module.user.api.v1.model.CompactUser;
import fm.mixer.gateway.module.user.api.v1.model.GetUserList;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.module.user.persistance.entity.UserFollower;
import fm.mixer.gateway.module.user.persistance.entity.UserFollower_;
import fm.mixer.gateway.module.user.persistance.entity.User_;
import fm.mixer.gateway.module.user.util.UserRelationUtil;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.Map;

@Mapper(imports = {PaginationMapper.class})
public interface UserCommunityMapper {

    @PaginatedMapping
    @Mapping(target = "users", source = "items.content", qualifiedByName = "toCompactUserFollowing")
    GetUserList toUserFollowingList(Page<UserFollower> items, PaginationRequest paginationRequest);

    @Named("toCompactUserFollowing")
    @Mapping(target = "username", source = "followsUser.identifier")
    @Mapping(target = "displayName", source = "followsUser.name")
    @Mapping(target = "avatarUrl", source = "followsUser.avatar")
    @Mapping(target = "profileColor", source = "followsUser.profileColor")
    @Mapping(target = "active", source = "followsUser.active")
    @Mapping(target = "relation", ignore = true)
    CompactUser toCompactUserFollowing(UserFollower userFollower);

    default Map<SortField, String> toFollowingColumnMapping() {
        return toColumnMapping(UserFollower_.FOLLOWS_USER);
    }

    @PaginatedMapping
    @Mapping(target = "users", source = "items.content", qualifiedByName = "toCompactUserFollowers")
    GetUserList toUserFollowerList(Page<UserFollower> items, PaginationRequest paginationRequest);

    @Named("toCompactUserFollowers")
    @Mapping(target = "username", source = "user.identifier")
    @Mapping(target = "displayName", source = "user.name")
    @Mapping(target = "avatarUrl", source = "user.avatar")
    @Mapping(target = "profileColor", source = "user.profileColor")
    @Mapping(target = "active", source = "user.active")
    @Mapping(target = "relation", ignore = true)
    CompactUser toCompactUserFollowers(UserFollower userFollower);

    default Map<SortField, String> toFollowerColumnMapping() {
        return toColumnMapping(UserFollower_.USER);
    }

    @AfterMapping
    default void updateRelations(@MappingTarget GetUserList userList) {
        UserRelationUtil.resolveRelation(userList.getUsers());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    UserFollower toUserFollower(User user, User followsUser);

    default Map<SortField, String> toColumnMapping(String prefix) {
        return Map.of(
            SortField.NAME, String.join(".", prefix, User_.NAME),
            SortField.DATE, UserFollower_.CREATED_AT,
            SortField.POPULARITY, String.join(".", prefix, User_.NUMBER_OF_FOLLOWERS)
            // SortField.TREND - who gain followers by day
        );
    }
}
