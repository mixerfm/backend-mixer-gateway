package fm.mixer.gateway.module.mix.mapper;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "avatarUrl", source = "avatar")
@Mapping(target = "numberOfLikes", expression = "java(collection.getReactions().size())")
@Mapping(target = "author", source = "user")
@Mapping(target = "reactions", expression = "java(ReactionMapper.toReactions(collection.getReactions()))")
@Mapping(target = "createdDate", source = "createdAt")
@Mapping(target = "updatedDate", source = "updatedAt")
public @interface MixCollectionCommonMapping {
}
