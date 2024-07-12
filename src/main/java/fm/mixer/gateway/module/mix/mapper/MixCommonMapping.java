package fm.mixer.gateway.module.mix.mapper;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "avatarUrl", source = "avatar")
@Mapping(target = "numberOfPlays", source = "playCount")
@Mapping(target = "numberOfLikes", expression = "java(mix.getReactions().size())")
@Mapping(target = "author", source = "user")
@Mapping(target = "type", source = ".", qualifiedByName = "toType")
@Mapping(target = "reactions", expression = "java(ReactionMapper.toReactions(mix.getReactions()))")
public @interface MixCommonMapping {
}
