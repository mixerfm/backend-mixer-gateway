package fm.mixer.gateway.module.community.mapper;

import fm.mixer.gateway.common.mapper.PaginatedMapping;
import fm.mixer.gateway.common.mapper.PaginationMapper;
import fm.mixer.gateway.common.model.PaginationRequest;
import fm.mixer.gateway.module.community.api.v1.model.Author;
import fm.mixer.gateway.module.community.api.v1.model.CommentList;
import fm.mixer.gateway.module.community.persistance.entity.Comment;
import fm.mixer.gateway.module.community.persistance.entity.CommentLike;
import fm.mixer.gateway.module.mix.persistance.entity.Mix;
import fm.mixer.gateway.module.user.persistance.entity.User;
import fm.mixer.gateway.util.RandomIdentifierUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(imports = {RandomIdentifierUtil.class, PaginationMapper.class})
public interface CommunityMapper {

    @Mapping(target = "author", source = "user")
    @Mapping(target = "createdDateTime", source = "createdAt")
    @Mapping(target = "updatedDateTime", source = "updatedAt")
    fm.mixer.gateway.module.community.api.v1.model.Comment toComment(Comment comment);

    @Mapping(target = "username", source = "identifier")
    @Mapping(target = "displayName", source = "name")
    @Mapping(target = "avatarUrl", source = "avatar")
    Author toAuthor(User user);

    @PaginatedMapping
    @Mapping(target = "comments", source = "items.content")
    CommentList toCommentList(Page<Comment> items, PaginationRequest paginationRequest);

    // MapStruct wrongly set variables if all are not stated (Bug in library)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "liked", source = "liked")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CommentLike toCommentLike(User user, Comment comment, Boolean liked);

    @Mapping(target = "content", source = "content")
    @Mapping(target = "mix", source = "mix")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "identifier", expression = "java(RandomIdentifierUtil.randomIdentifier())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Comment toComment(String content, User user, Mix mix);
}
