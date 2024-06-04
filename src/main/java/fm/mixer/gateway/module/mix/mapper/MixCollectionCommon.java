package fm.mixer.gateway.module.mix.mapper;

import org.mapstruct.Mapping;

@Mapping(target = "avatarUrl", source = "avatar")
@Mapping(target = "author", source = "user")
@Mapping(target = "liked", ignore = true) // TODO add value
public @interface MixCollectionCommon {
}
