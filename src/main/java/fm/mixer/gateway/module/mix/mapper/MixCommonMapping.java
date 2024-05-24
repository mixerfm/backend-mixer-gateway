package fm.mixer.gateway.module.mix.mapper;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "avatarUrl", source = "avatar")
@Mapping(target = "numberOfPlays", constant = "0") // TODO add value
@Mapping(target = "numberOfLikes", constant = "0") // TODO add value
@Mapping(target = "numberOfTracks", expression = "java(mix.getTracks().size())")
@Mapping(target = "duration", source = "tracks")
@Mapping(target = "author", source = "user")
@Mapping(target = "tags", ignore = true) // TODO add value
public @interface MixCommonMapping {
}
