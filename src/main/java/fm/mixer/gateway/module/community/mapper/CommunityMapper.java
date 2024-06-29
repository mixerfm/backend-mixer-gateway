package fm.mixer.gateway.module.community.mapper;

import fm.mixer.gateway.auth.util.UserPrincipalUtil;
import fm.mixer.gateway.common.mapper.CreatorCommonMapping;
import fm.mixer.gateway.common.mapper.PaginatedMapping;
import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.module.community.api.v1.model.CommentList;
import fm.mixer.gateway.module.community.api.v1.model.Creator;
import fm.mixer.gateway.module.community.api.v1.model.UserReaction;
import fm.mixer.gateway.module.community.persistance.entity.Comment;
import fm.mixer.gateway.module.community.persistance.entity.CommentLike;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.util.RandomIdentifierUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

@Mapper(imports = {RandomIdentifierUtil.class, PaginationMapper.class})
public interface CommunityMapper {

    @Mapping(target = "author", source = "user")
    @Mapping(target = "createdDateTime", source = "createdAt")
    @Mapping(target = "updatedDateTime", source = "updatedAt")
    @Mapping(target = "reactions", source = "likes", qualifiedByName = "toReactions")
    @Mapping(target = "numberOfLikes", source = "likes", qualifiedByName = "toNumberOfLikes")
    @Mapping(target = "numberOfDislikes", source = "likes", qualifiedByName = "toNumberOfDislikes")
    fm.mixer.gateway.module.community.api.v1.model.Comment toComment(final Comment comment);

    @Named("toNumberOfLikes")
    default Integer toNumberOfLikes(final Set<CommentLike> likes) {
        return (int) likes.stream().filter(CommentLike::getLiked).count();
    }

    @Named("toNumberOfDislikes")
    default Integer toNumberOfDislikes(final Set<CommentLike> likes) {
        return (int) likes.stream().filter(commentLike -> !commentLike.getLiked()).count();
    }

    @Named("toReactions")
    default List<UserReaction> toReactions(final Set<CommentLike> likes) {
        final var currentActiveUser = UserPrincipalUtil.getCurrentActiveUser();

        return currentActiveUser.map(user -> likes.stream()
                .filter(like -> user.getId().equals(like.getUser().getId()))
                .map(like -> like.getLiked() ? UserReaction.TypeEnum.LIKE : UserReaction.TypeEnum.DISLIKE)
                .map(UserReaction::new)
                .toList()
            )
            .orElse(List.of());
    }

    @CreatorCommonMapping
    Creator toAuthor(User user);

    @PaginatedMapping
    @Mapping(target = "comments", source = "items.content")
    CommentList toCommentList(Page<Comment> items, PaginationRequest paginationRequest);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "liked", source = "liked")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CommentLike toCommentLikeEntity(User user, Comment comment, Boolean liked);

    @Mapping(target = "content", source = "content")
    @Mapping(target = "mix", source = "mix")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "identifier", expression = "java(RandomIdentifierUtil.randomIdentifier())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Comment toCommentEntity(String content, User user, Mix mix);
}
