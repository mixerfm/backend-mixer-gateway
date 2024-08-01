package fm.mixer.gateway.module.community.mapper;

import fm.mixer.gateway.common.mapper.CreatorCommonMapping;
import fm.mixer.gateway.common.mapper.PaginatedMapping;
import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.module.community.api.v1.model.Comment;
import fm.mixer.gateway.module.community.api.v1.model.CommentList;
import fm.mixer.gateway.module.community.api.v1.model.Creator;
import fm.mixer.gateway.module.community.persistance.entity.CommentEntity;
import fm.mixer.gateway.module.community.persistance.entity.CommentLike;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.react.persistance.mapper.ReactionMapper;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.common.util.RandomIdentifierUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.Set;

@Mapper(imports = {RandomIdentifierUtil.class, PaginationMapper.class, ReactionMapper.class})
public interface CommunityMapper {

    @Mapping(target = "author", source = "user")
    @Mapping(target = "createdDateTime", source = "createdAt")
    @Mapping(target = "updatedDateTime", source = "updatedAt")
    @Mapping(target = "reactions", expression = "java(ReactionMapper.toReactions(comment.getReactions()))")
    @Mapping(target = "numberOfLikes", source = "reactions", qualifiedByName = "toNumberOfLikes")
    @Mapping(target = "numberOfDislikes", source = "reactions", qualifiedByName = "toNumberOfDislikes")
    Comment toComment(final CommentEntity comment);

    @Named("toNumberOfLikes")
    default Integer toNumberOfLikes(final Set<CommentLike> reactions) {
        return (int) reactions.stream().filter(CommentLike::getValue).count();
    }

    @Named("toNumberOfDislikes")
    default Integer toNumberOfDislikes(final Set<CommentLike> reactions) {
        return (int) reactions.stream().filter(commentLike -> !commentLike.getValue()).count();
    }

    @CreatorCommonMapping
    Creator toAuthor(User user);

    @PaginatedMapping
    @Mapping(target = "comments", source = "items.content")
    CommentList toCommentList(Page<CommentEntity> items, PaginationRequest paginationRequest);

    @Mapping(target = "content", source = "content")
    @Mapping(target = "mix", source = "mix")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "numberOfReplies", constant = "0")
    @Mapping(target = "identifier", expression = "java(RandomIdentifierUtil.randomIdentifier())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    CommentEntity toCommentEntity(String content, User user, Mix mix);
}
